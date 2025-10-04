package com.example.suarasamind.app.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.viewbinding.ViewBinding
import com.example.suarasamind.app.R
import com.example.suarasamind.app.auth.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB
    protected lateinit var firebaseAuth: FirebaseAuth
    protected lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Auth check
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Setup ViewBinding
        binding = inflateBinding()
        setContentView(binding.root)
    }

    abstract fun inflateBinding(): VB

    /**
     * Setup Bottom Navigation dengan NavController (Versi Paling Stabil)
     * Menghubungkan NavView dengan NavController secara otomatis.
     */
    protected fun setupBottomNavigation(navView: BottomNavigationView) {
        try {
            // Ambil NavHostFragment dari layout
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController

            // Auto sinkron dengan Navigation Component
            NavigationUI.setupWithNavController(navView, navController)

        } catch (e: Exception) {
            // Jika terjadi error, catat di Logcat
            e.printStackTrace()
        }
    }
}