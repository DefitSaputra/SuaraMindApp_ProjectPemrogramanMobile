package com.example.suarasamind.app.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.suarasamind.app.main.MainActivity
import com.example.suarasamind.app.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore // [TAMBAHAN] Import untuk Firestore
import com.google.firebase.messaging.FirebaseMessaging // [TAMBAHAN] Import untuk FCM

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.tvRegisterNow.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            navigateToHome()
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()

                    FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                        if (!tokenTask.isSuccessful) {
                            Log.w("FCM", "Fetching FCM registration token failed", tokenTask.exception)
                            navigateToHome()
                            return@addOnCompleteListener
                        }
                        val token = tokenTask.result
                        Log.d("FCM", "FCM Token fetched: $token")
                        sendTokenToFirestore(token)
                        navigateToHome()
                    }
                } else {
                    Toast.makeText(this, "Login gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun sendTokenToFirestore(token: String) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            Log.w("FCM", "Cannot save token, user is null.")
            return
        }

        val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userDocRef.update("fcmToken", token)
            .addOnSuccessListener { Log.d("FCM", "Token successfully updated in Firestore.") }
            .addOnFailureListener { e -> Log.w("FCM", "Error updating token in Firestore", e) }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}