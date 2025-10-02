package com.example.suarasamind.app

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.adapters.CommentAdapter
import com.example.suarasamind.app.data.Comment
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.databinding.ActivityPostDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PostDetailActivity : BaseActivity<ActivityPostDetailBinding>() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var commentAdapter: CommentAdapter
    private val commentList = mutableListOf<Comment>()
    private lateinit var postId: String
    private lateinit var currentUserId: String

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
        loadPostDetails()
        listenToComments()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.fabAddComment.setOnClickListener {
            showAddCommentDialog()
        }
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter(commentList)
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(this@PostDetailActivity)
            adapter = commentAdapter
        }
    }

    private fun loadPostDetails() {
        firestore.collection("posts").document(postId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val post = document.toObject(ForumPost::class.java)
                    post?.let {
                        binding.tvPostTitle.text = it.title
                        binding.tvPostAuthor.text = "oleh ${it.authorUsername}"
                        binding.tvPostContent.text = it.content
                        binding.tvSupportCount.text = it.supportCount.toString()
                        binding.tvCommentCount.text = it.commentCount.toString()

                        // Format tanggal
                        it.timestamp?.let { timestamp ->
                            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                            binding.tvPostDate.text = "• ${sdf.format(timestamp)}"
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat detail post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun listenToComments() {
        firestore.collection("posts").document(postId)
            .collection("comments")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    commentList.clear()
                    for (document in snapshots) {
                        val comment = document.toObject(Comment::class.java)
                        comment.id = document.id
                        commentList.add(comment)
                    }
                    commentAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun showAddCommentDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
        val input = android.widget.EditText(this)
        input.hint = "Tulis komentar..."

        dialog.setTitle("Tambah Komentar")
        dialog.setView(input)

        dialog.setPositiveButton("Kirim") { _, _ ->
            val commentText = input.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
            }
        }

        dialog.setNegativeButton("Batal", null)
        dialog.show()
    }

    private fun addComment(commentText: String) {
        val comment = Comment(
            authorId = currentUserId,
            authorUsername = "Pengguna", // Bisa diambil dari Firestore
            content = commentText
        )

        firestore.collection("posts").document(postId)
            .collection("comments")
            .add(comment)
            .addOnSuccessListener {
                // Update comment count di post
                firestore.collection("posts").document(postId)
                    .update("commentCount", com.google.firebase.firestore.FieldValue.increment(1))

                Toast.makeText(this, "Komentar berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menambah komentar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}