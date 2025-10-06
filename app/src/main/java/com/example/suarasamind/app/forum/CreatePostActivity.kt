package com.example.suarasamind.app.forum

import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.databinding.ActivityCreatePostBinding
import com.example.suarasamind.app.main.BaseActivity
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

        // Atur tampilan awal UI untuk switch saat activity pertama kali dibuat
        updateAnonymousUI(binding.switchAnonymous.isChecked)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.btnSubmitPost.setOnClickListener {
            submitPost()
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }

        // PERUBAHAN: Tambahkan listener untuk mendeteksi perubahan pada switch
        binding.switchAnonymous.setOnCheckedChangeListener { _, isChecked ->
            updateAnonymousUI(isChecked)
        }
    }

    // FUNGSI BARU: Untuk mengupdate UI berdasarkan status switch
    private fun updateAnonymousUI(isAnonymous: Boolean) {
        if (isAnonymous) {
            // Saat mode Anonim AKTIF
            binding.tvAnonymousStatus.text = "Mode anonim: AKTIF"
            binding.cardAnonymous.strokeColor = ContextCompat.getColor(this, R.color.calm_blue)
            binding.ivAnonymousIcon.setColorFilter(ContextCompat.getColor(this, R.color.calm_blue))
        } else {
            // Saat mode Anonim NONAKTIF
            binding.tvAnonymousStatus.text = "Identitas sebagai ${currentUsername} akan ditampilkan"
            binding.cardAnonymous.strokeColor = ContextCompat.getColor(this, R.color.text_super_light)
            binding.ivAnonymousIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_light))
        }
    }

    private fun fetchCurrentUser() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        currentUsername = document.getString("username") ?: "Pengguna"
                        // Setelah username didapat, update UI lagi untuk menampilkan nama pengguna
                        // jika mode anonim sedang nonaktif.
                        updateAnonymousUI(binding.switchAnonymous.isChecked)
                    }
                }
        }
    }

    private fun submitPost() {
        val title = binding.etPostTitle.text?.toString()?.trim() ?: ""
        val content = binding.etPostContent.text?.toString()?.trim() ?: ""
        val isAnonymous = binding.switchAnonymous.isChecked
        val authorId = firebaseAuth.currentUser?.uid

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
                binding.btnSubmitPost.isEnabled = true
                binding.btnSubmitPost.text = "Bagikan Cerita"
            }
    }
}

