package com.example.suarasamind.app.forum

import android.os.Bundle
import android.widget.Toast
import com.example.suarasamind.app.main.BaseActivity
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.databinding.ActivityCreatePostBinding
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostActivity : BaseActivity<ActivityCreatePostBinding>() { // Perbaikan 1: Warisi BaseActivity

    private lateinit var firestore: FirebaseFirestore
    private var currentUsername = "Anonim"

    override fun inflateBinding(): ActivityCreatePostBinding {
        return ActivityCreatePostBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        fetchCurrentUser()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSubmitPost.setOnClickListener {
            submitPost()
        }
    }

    private fun fetchCurrentUser() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        currentUsername = document.getString("username") ?: "Pengguna"
                    }
                }
        }
    }

    private fun submitPost() {
        // Perbaikan 2: Ambil teks dari TextInputEditText dengan benar
        val title = binding.etPostTitle.text?.toString()?.trim() ?: ""
        val content = binding.etPostContent.text?.toString()?.trim() ?: ""
        val isAnonymous = binding.switchAnonymous.isChecked
        val authorId = firebaseAuth.currentUser?.uid

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Judul dan isi cerita tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (authorId == null) {
            Toast.makeText(this, "Gagal mendapatkan data pengguna, silakan login ulang", Toast.LENGTH_SHORT).show()
            return
        }

        val authorName = if (isAnonymous) "Anonim" else currentUsername

        val newPost = ForumPost(
            authorId = authorId,
            authorUsername = authorName,
            title = title,
            content = content
        )

        firestore.collection("posts")
            .add(newPost)
            .addOnSuccessListener {
                Toast.makeText(this, "Ceritamu berhasil dibagikan!", Toast.LENGTH_SHORT).show()
                finish() // Kembali ke halaman forum
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal membagikan cerita: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}