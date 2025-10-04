package com.example.suarasamind.app.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.suarasamind.app.databinding.ActivityEditProfileBinding
import com.example.suarasamind.app.main.BaseActivity

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>() {

    // Kita akan gunakan ViewModel yang sama dengan ProfileFragment
    // (ini kurang ideal, tapi cara termudah untuk sekarang)
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun inflateBinding(): ActivityEditProfileBinding {
        return ActivityEditProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil nama saat ini yang dikirim dari ProfileFragment
        val currentName = intent.getStringExtra("CURRENT_NAME")
        binding.etFullName.setText(currentName)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etFullName.text.toString().trim()
            if (newName.isNotEmpty()) {
                profileViewModel.updateUserName(newName)
                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish() // Tutup halaman setelah menyimpan
            } else {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}