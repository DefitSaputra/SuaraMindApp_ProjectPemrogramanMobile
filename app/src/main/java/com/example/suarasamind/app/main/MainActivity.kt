package com.example.suarasamind.app.main

import android.os.Bundle
import com.example.suarasamind.app.main.BaseActivity
import com.example.suarasamind.app.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomNavigation(binding.bottomNavView)
    }
}