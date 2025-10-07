package com.example.suarasamind.app.profile

import android.os.Bundle
import android.widget.Toast
import com.example.suarasamind.app.databinding.ActivityChangePasswordBinding
import com.example.suarasamind.app.main.BaseActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : BaseActivity<ActivityChangePasswordBinding>() {

    private lateinit var auth: FirebaseAuth

    override fun inflateBinding(): ActivityChangePasswordBinding {
        return ActivityChangePasswordBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnSavePassword.setOnClickListener {
            updatePassword()
        }
    }

    private fun updatePassword() {
        val currentPassword = binding.etCurrentPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "Password baru minimal 6 karakter", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Password baru dan konfirmasi tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user?.email == null) {
            Toast.makeText(this, "Pengguna tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Password berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal memperbarui: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Password saat ini salah", Toast.LENGTH_LONG).show()
            }
    }
}