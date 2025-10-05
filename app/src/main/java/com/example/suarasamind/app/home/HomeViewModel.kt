package com.example.suarasamind.app.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suarasamind.app.data.ContentData
import com.example.suarasamind.app.data.ForumPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
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

    private var postsListener: ListenerRegistration? = null
    private var articlesListener: ListenerRegistration? = null

    init {
        loadUserProfile()
        listenToArticles()
        listenToPosts()
    }

    private fun loadUserProfile() {
        if (currentUserId.isEmpty()) return
        firestore.collection("users").document(currentUserId).get()
            .addOnSuccessListener { document ->
                val fullName = document.getString("fullName") ?: "Sobat"
                val hour = SimpleDateFormat("HH", Locale.getDefault()).format(Date()).toInt()
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

    private fun listenToArticles() {
        articlesListener = firestore.collection("articles").addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.w("HomeViewModel", "Error getting articles", error)
                _articles.value = emptyList()
                return@addSnapshotListener
            }
            if (snapshots != null) {
                val articleList = snapshots.map { it.toObject<ContentData>() }
                _articles.value = articleList
            }
        }
    }

    private fun listenToPosts() {
        postsListener = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(3)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("HomeViewModel", "Error getting posts", error)
                    _forumPosts.value = emptyList()
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val postList = snapshots.map { document ->
                        document.toObject(ForumPost::class.java).apply { id = document.id }
                    }
                    _forumPosts.value = postList
                }
            }
    }

    fun toggleSupport(post: ForumPost) {
        if (currentUserId.isEmpty()) return
        val postRef = firestore.collection("posts").document(post.id)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val forumPost = snapshot.toObject<ForumPost>() ?: return@runTransaction
            val supporters = forumPost.supporters.toMutableList()
            if (supporters.contains(currentUserId)) {
                transaction.update(postRef, "supportCount", FieldValue.increment(-1))
                transaction.update(postRef, "supporters", FieldValue.arrayRemove(currentUserId))
            } else {
                transaction.update(postRef, "supportCount", FieldValue.increment(1))
                transaction.update(postRef, "supporters", FieldValue.arrayUnion(currentUserId))
            }
        }.addOnFailureListener { e -> Log.w("HomeViewModel", "Error toggling support", e) }
    }

    fun saveMood(moodType: String) {
        if (currentUserId.isEmpty()) return

        val moodData = hashMapOf(
            "type" to moodType,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("users").document(currentUserId)
            .collection("moods")
            .add(moodData)
            .addOnSuccessListener {
                Log.d("HomeViewModel", "Mood '$moodType' saved successfully.")
            }
            .addOnFailureListener { e ->
                Log.w("HomeViewModel", "Error saving mood", e)
            }
    }

    override fun onCleared() {
        super.onCleared()
        postsListener?.remove()
        articlesListener?.remove()
    }
}