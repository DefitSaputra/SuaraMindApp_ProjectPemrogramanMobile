package com.example.suarasamind.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
// Perbaikan: Impor class binding yang benar
import com.example.suarasamind.app.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    // Perbaikan 1: Gunakan ActivityOnboardingBinding, bukan ActivityMainBinding
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private lateinit var autoScrollRunnable: Runnable
    private val SCROLL_DELAY_MS = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Perbaikan 2: Inflate layout yang benar
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onboardingAdapter = OnboardingAdapter(this)
        binding.onboardingViewPager.adapter = onboardingAdapter

        binding.dotsIndicator.attachTo(binding.onboardingViewPager)

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        setupAutoScroll()
    }

    private fun setupAutoScroll() {
        autoScrollRunnable = Runnable {
            var currentItem = binding.onboardingViewPager.currentItem
            val totalItems = onboardingAdapter.itemCount
            currentItem = (currentItem + 1) % totalItems
            binding.onboardingViewPager.setCurrentItem(currentItem, true)
            autoScrollHandler.postDelayed(autoScrollRunnable, SCROLL_DELAY_MS)
        }
    }

    override fun onResume() {
        super.onResume()
        autoScrollHandler.postDelayed(autoScrollRunnable, SCROLL_DELAY_MS)
    }

    override fun onPause() {
        super.onPause()
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }
}