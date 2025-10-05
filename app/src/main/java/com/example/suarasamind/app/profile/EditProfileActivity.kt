package com.example.suarasamind.app.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.suarasamind.app.databinding.ActivityEditProfileBinding
import com.example.suarasamind.app.main.BaseActivity

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>() {

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun inflateBinding(): ActivityEditProfileBinding {
        return ActivityEditProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                finish()
            } else {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}