package com.example.suarasamind.app.forum

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.main.BaseActivity
import com.example.suarasamind.app.adapters.CommentAdapter
import com.example.suarasamind.app.adapters.PostDetailHeaderAdapter
import com.example.suarasamind.app.data.Comment
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.databinding.ActivityPostDetailBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class PostDetailActivity : BaseActivity<ActivityPostDetailBinding>() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var postId: String
    private lateinit var currentUserId: String

    // Adapter baru
    private lateinit var headerAdapter: PostDetailHeaderAdapter
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var concatAdapter: ConcatAdapter

    private val commentList = mutableListOf<Comment>()

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
        listenToPostDetails() // Mengganti loadPostDetails menjadi listener
        listenToComments()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Listener untuk tombol kirim komentar yang baru
        binding.btnSendComment.setOnClickListener {
            val commentText = binding.etComment.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
            } else {
                Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        // Inisialisasi kedua adapter
        headerAdapter = PostDetailHeaderAdapter(currentUserId)
        commentAdapter = CommentAdapter(commentList)

        // Gabungkan keduanya dengan ConcatAdapter
        concatAdapter = ConcatAdapter(headerAdapter, commentAdapter)

        binding.rvPostDetail.apply { // Menggunakan ID RecyclerView utama yang baru
            layoutManager = LinearLayoutManager(this@PostDetailActivity)
            adapter = concatAdapter
        }

        // Menambahkan listener untuk tombol support/like yang ada di header
        headerAdapter.onSupportClick = {
            toggleSupport()
        }
    }

    private fun listenToPostDetails() {
        // Gunakan addSnapshotListener agar data post (termasuk jumlah like) otomatis update
        firestore.collection("posts").document(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Gagal memuat detail post: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val post = snapshot.toObject(ForumPost::class.java)
                    post?.let {
                        // Kirim data post ke header adapter
                        it.id = snapshot.id
                        headerAdapter.setPost(it)
                    }
                }
            }
    }

    private fun listenToComments() {
        firestore.collection("posts").document(postId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("PostDetailActivity", "Listen comments failed.", error)
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

    private fun addComment(commentText: String) {
        val comment = Comment(
            authorId = currentUserId,
            authorUsername = "Pengguna", // TODO: Ambil nama pengguna saat ini dari Firestore/SharedPreferences
            content = commentText
        )

        firestore.collection("posts").document(postId)
            .collection("comments")
            .add(comment)
            .addOnSuccessListener {
                firestore.collection("posts").document(postId)
                    .update("commentCount", FieldValue.increment(1))
                binding.etComment.text.clear() // Bersihkan input field
                Toast.makeText(this, "Komentar berhasil ditambahkan", Toast.LENGTH_SHORT).show()
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
                // User sudah support, jadi batalkan (unlike)
                transaction.update(postRef, "supportCount", FieldValue.increment(-1))
                transaction.update(postRef, "supporters", FieldValue.arrayRemove(currentUserId))
            } else {
                // User belum support, jadi tambahkan (like)
                transaction.update(postRef, "supportCount", FieldValue.increment(1))
                transaction.update(postRef, "supporters", FieldValue.arrayUnion(currentUserId))
            }
        }.addOnFailureListener { e ->
            Log.w("PostDetailActivity", "Error toggling support", e)
            Toast.makeText(this, "Gagal memberikan dukungan", Toast.LENGTH_SHORT).show()
        }
    }
}