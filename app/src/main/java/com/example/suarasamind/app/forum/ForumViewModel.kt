package com.example.suarasamind.app.forum

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suarasamind.app.data.ForumPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class ForumViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String get() = auth.currentUser?.uid ?: ""

    private val _posts = MutableLiveData<List<ForumPost>>()
    val posts: LiveData<List<ForumPost>> = _posts

    private var postsListener: ListenerRegistration? = null

    init {
        listenToAllPosts()
    }

    private fun listenToAllPosts() {
        postsListener?.remove()

        postsListener = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("ForumViewModel", "Listen failed.", error)
                    _posts.value = emptyList()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val postList = snapshots.map { document ->
                        document.toObject(ForumPost::class.java).apply { id = document.id }
                    }
                    _posts.value = postList
                }
            }
    }

    fun toggleSupport(post: ForumPost) {
        if (currentUserId.isEmpty()) return

        val postRef = firestore.collection("posts").document(post.id)

        if (post.supporters.contains(currentUserId)) {
            postRef.update("supporters", FieldValue.arrayRemove(currentUserId))
        } else {
            postRef.update("supporters", FieldValue.arrayUnion(currentUserId))
        }
    }

    override fun onCleared() {
        super.onCleared()
        postsListener?.remove()
    }
}