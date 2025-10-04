package com.example.suarasamind.app.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.suarasamind.app.auth.LoginActivity
import com.example.suarasamind.app.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase Auth dan Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Listener untuk tombol kembali
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Listener untuk teks "Login di sini"
        binding.tvLoginNow.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Listener untuk tombol Register
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        // Ambil semua data dari form
        val fullName = binding.etFullname.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validasi input
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        // Buat user baru di Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Jika registrasi auth berhasil, simpan data tambahan ke Firestore
                    saveUserDataToFirestore(fullName, username, email)
                } else {
                    // Jika registrasi gagal, tampilkan pesan error
                    Toast.makeText(this, "Registrasi gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserDataToFirestore(fullName: String, username: String, email: String) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val userMap = hashMapOf(
                "fullName" to fullName,
                "username" to username,
                "email" to email
            )

            // Simpan data ke collection "users" dengan dokumen ID sesuai UID pengguna
            firestore.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    // Arahkan ke halaman Login setelah sukses
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity() // Membersihkan semua activity sebelumnya
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menyimpan data pengguna: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}