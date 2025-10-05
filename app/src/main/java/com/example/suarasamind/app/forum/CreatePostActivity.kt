package com.example.suarasamind.app.forum

import android.os.Bundle
import android.widget.Toast
import com.example.suarasamind.app.main.BaseActivity
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.databinding.ActivityCreatePostBinding
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostActivity : BaseActivity<ActivityCreatePostBinding>() {

    private lateinit var firestore: FirebaseFirestore
    private var currentUsername = "Anonim"

    override fun inflateBinding(): ActivityCreatePostBinding {
        return ActivityCreatePostBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        fetchCurrentUser()
        setupListeners()
    }

    private fun setupListeners() {
        // Tombol back di toolbar
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Tombol submit
        binding.btnSubmitPost.setOnClickListener {
            submitPost()
        }

        // Tombol cancel
        binding.btnCancel.setOnClickListener {
            finish()
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
        val title = binding.etPostTitle.text?.toString()?.trim() ?: ""
        val content = binding.etPostContent.text?.toString()?.trim() ?: ""
        val isAnonymous = binding.switchAnonymous.isChecked
        val authorId = firebaseAuth.currentUser?.uid

        // Validasi input
        if (title.isEmpty()) {
            binding.tilPostTitle.error = "Judul tidak boleh kosong"
            return
        } else {
            binding.tilPostTitle.error = null
        }

        if (content.isEmpty()) {
            binding.tilPostContent.error = "Cerita tidak boleh kosong"
            return
        } else {
            binding.tilPostContent.error = null
        }

        if (content.length < 20) {
            binding.tilPostContent.error = "Cerita minimal 20 karakter"
            return
        } else {
            binding.tilPostContent.error = null
        }

        if (authorId == null) {
            Toast.makeText(this, "Gagal mendapatkan data pengguna, silakan login ulang", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable button saat proses upload
        binding.btnSubmitPost.isEnabled = false
        binding.btnSubmitPost.text = "Mengirim..."

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
                Toast.makeText(this, "Ceritamu berhasil dibagikan! 🎉", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal membagikan cerita: ${e.message}", Toast.LENGTH_LONG).show()
                // Re-enable button jika gagal
                binding.btnSubmitPost.isEnabled = true
                binding.btnSubmitPost.text = "Bagikan Cerita"
            }
    }
}