package com.example.suarasamind.app.forum

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.adapters.CommentAdapter
import com.example.suarasamind.app.adapters.PostDetailHeaderAdapter
import com.example.suarasamind.app.data.Comment
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.databinding.ActivityPostDetailBinding
import com.example.suarasamind.app.main.BaseActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class PostDetailActivity : BaseActivity<ActivityPostDetailBinding>() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var postId: String
    private lateinit var currentUserId: String
    private var currentUsername = "Pengguna"

    private lateinit var headerAdapter: PostDetailHeaderAdapter
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var concatAdapter: ConcatAdapter

    private val commentList = mutableListOf<Comment>()

    private var postListener: ListenerRegistration? = null
    private var commentsListener: ListenerRegistration? = null

    override fun inflateBinding(): ActivityPostDetailBinding {
        return ActivityPostDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        currentUserId = firebaseAuth.currentUser?.uid ?: ""

        postId = intent.getStringExtra("POST_ID") ?: ""
        if (postId.isEmpty()) {
            Toast.makeText(this, "Post tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        fetchCurrentUser()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSendComment.setOnClickListener {
            val commentText = binding.etComment.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
            } else {
                Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        listenToPostDetails()
        listenToComments()
    }

    override fun onStop() {
        super.onStop()
        postListener?.remove()
        commentsListener?.remove()
    }

    private fun fetchCurrentUser() {
        if (currentUserId.isNotEmpty()) {
            firestore.collection("users").document(currentUserId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        currentUsername = document.getString("username") ?: "Pengguna"
                    }
                }
        }
    }

    private fun setupRecyclerView() {
        headerAdapter = PostDetailHeaderAdapter(currentUserId)
        commentAdapter = CommentAdapter(commentList)
        concatAdapter = ConcatAdapter(headerAdapter, commentAdapter)

        binding.rvPostDetail.apply {
            layoutManager = LinearLayoutManager(this@PostDetailActivity)
            adapter = concatAdapter
        }

        headerAdapter.onSupportClick = {
            toggleSupport()
        }
    }

    private fun listenToPostDetails() {
        postListener = firestore.collection("posts").document(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Gagal memuat detail post: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val post = snapshot.toObject(ForumPost::class.java)
                    post?.let {
                        it.id = snapshot.id
                        headerAdapter.setPost(it)
                    }
                } else {
                    Toast.makeText(this, "Cerita ini telah dihapus.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
    }

    private fun listenToComments() {
        commentsListener = firestore.collection("posts").document(postId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("PostDetailActivity", "Listen comments failed.", error)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val previousCount = commentList.size
                    commentList.clear()
                    for (document in snapshots) {
                        val comment = document.toObject(Comment::class.java)
                        comment.id = document.id
                        commentList.add(comment)
                    }
                    commentAdapter.notifyDataSetChanged()

                    if (commentList.size > previousCount) {
                        scrollToBottom()
                    }
                }
            }
    }

    private fun scrollToBottom() {
        if (concatAdapter.itemCount > 0) {
            binding.rvPostDetail.post {
                binding.rvPostDetail.smoothScrollToPosition(concatAdapter.itemCount - 1)
            }
        }
    }

    private fun addComment(commentText: String) {
        val comment = Comment(authorId = currentUserId, authorUsername = currentUsername, content = commentText)
        firestore.collection("posts").document(postId).collection("comments").add(comment)
            .addOnSuccessListener {
                firestore.collection("posts").document(postId).update("commentCount", FieldValue.increment(1))
                binding.etComment.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menambah komentar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleSupport() {
        val postRef = firestore.collection("posts").document(postId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val post = snapshot.toObject<ForumPost>() ?: return@runTransaction
            val supporters = post.supporters.toMutableList()
            if (supporters.contains(currentUserId)) {
                transaction.update(postRef, "supportCount", FieldValue.increment(-1))
                transaction.update(postRef, "supporters", FieldValue.arrayRemove(currentUserId))
            } else {
                transaction.update(postRef, "supportCount", FieldValue.increment(1))
                transaction.update(postRef, "supporters", FieldValue.arrayUnion(currentUserId))
            }
        }.addOnFailureListener { e ->
            Log.w("PostDetailActivity", "Error toggling support", e)
            Toast.makeText(this, "Gagal memberikan dukungan", Toast.LENGTH_SHORT).show()
        }
    }
}

