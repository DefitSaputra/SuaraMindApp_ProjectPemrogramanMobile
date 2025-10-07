package com.example.suarasamind.app.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suarasamind.app.data.ContentData
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.data.MoodEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String get() = auth.currentUser?.uid ?: ""

    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> = _greeting

    private val _articles = MutableLiveData<List<ContentData>>()
    val articles: LiveData<List<ContentData>> = _articles

    private val _forumPosts = MutableLiveData<List<ForumPost>>()
    val forumPosts: LiveData<List<ForumPost>> = _forumPosts

    private val _hasMoodToday = MutableLiveData<Boolean>()
    val hasMoodToday: LiveData<Boolean> = _hasMoodToday

    private var postsListener: ListenerRegistration? = null
    private var articlesListener: ListenerRegistration? = null
    private var moodTodayListener: ListenerRegistration? = null

    init {
        loadUserProfile()
        listenToArticles()
        listenToPosts()
        checkMoodToday()
    }

    private fun loadUserProfile() {
        if (currentUserId.isEmpty()) {
            _greeting.value = "Selamat Datang,\nSobat!"
            return
        }
        firestore.collection("users").document(currentUserId).get()
            .addOnSuccessListener { document ->
                val fullName = document.getString("fullName")?.trim() ?: "Sobat"
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val greetingPrefix = when (hour) {
                    in 5..11 -> "Selamat Pagi,"
                    in 12..15 -> "Selamat Siang,"
                    in 16..18 -> "Selamat Sore,"
                    else -> "Selamat Malam,"
                }
                _greeting.value = "$greetingPrefix\n$fullName!"
            }
            .addOnFailureListener {
                _greeting.value = "Selamat Datang,\nSobat!"
            }
    }

    private fun checkMoodToday() {
        if (currentUserId.isEmpty()) return

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.time

        moodTodayListener = firestore.collection("users").document(currentUserId)
            .collection("moods")
            .whereGreaterThanOrEqualTo("timestamp", startOfDay)
            .whereLessThan("timestamp", endOfDay)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    _hasMoodToday.value = false
                    Log.w("HomeViewModel", "Error checking mood today", error)
                    return@addSnapshotListener
                }
                _hasMoodToday.value = snapshots != null && !snapshots.isEmpty
            }
    }

    fun saveMood(moodType: String) {
        if (currentUserId.isEmpty() || _hasMoodToday.value == true) return

        val moodEntry = MoodEntry(type = moodType, timestamp = Timestamp.now().toDate())

        firestore.collection("users").document(currentUserId)
            .collection("moods")
            .add(moodEntry)
            .addOnSuccessListener {
                Log.d("HomeViewModel", "Mood '$moodType' saved successfully.")
            }
            .addOnFailureListener { e ->
                Log.w("HomeViewModel", "Error saving mood", e)
            }
    }

    private fun listenToArticles() {
        articlesListener = firestore.collection("articles").addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.w("HomeViewModel", "Error getting articles", error)
                return@addSnapshotListener
            }
            _articles.value = snapshots?.map { it.toObject<ContentData>() } ?: emptyList()
        }
    }

    private fun listenToPosts() {
        postsListener = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(3)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("HomeViewModel", "Error getting posts", error)
                    return@addSnapshotListener
                }
                _forumPosts.value = snapshots?.map { document ->
                    document.toObject(ForumPost::class.java).apply { id = document.id }
                } ?: emptyList()
            }
    }

    fun toggleSupport(post: ForumPost) {
        if (currentUserId.isEmpty()) return
        val postRef = firestore.collection("posts").document(post.id)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val supporters = snapshot.get("supporters") as? List<String> ?: emptyList()
            if (supporters.contains(currentUserId)) {
                transaction.update(postRef, "supportCount", FieldValue.increment(-1))
                transaction.update(postRef, "supporters", FieldValue.arrayRemove(currentUserId))
            } else {
                transaction.update(postRef, "supportCount", FieldValue.increment(1))
                transaction.update(postRef, "supporters", FieldValue.arrayUnion(currentUserId))
            }
        }.addOnFailureListener { e -> Log.w("HomeViewModel", "Error toggling support", e) }
    }

    override fun onCleared() {
        super.onCleared()
        postsListener?.remove()
        articlesListener?.remove()
        moodTodayListener?.remove()
    }
}