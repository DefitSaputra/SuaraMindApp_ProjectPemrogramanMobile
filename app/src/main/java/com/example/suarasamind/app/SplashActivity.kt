// File: SplashActivity.kt (VERSI YANG SUDAH DIPERBAIKI)

package com.example.suarasamind.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var isSessionChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        splashScreen.setKeepOnScreenCondition { !isSessionChecked }
        checkUserSession()
    }

    private fun checkUserSession() {
        val intent = if (firebaseAuth.currentUser != null) {
            // PERBAIKAN: Arahkan ke MainActivity yang benar!
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, OnboardingActivity::class.java)
        }
        isSessionChecked = true
        startActivity(intent)
        finish()
    }
}