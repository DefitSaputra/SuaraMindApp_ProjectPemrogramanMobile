package com.example.suarasamind.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.suarasamind.app.auth.LoginActivity
import com.example.suarasamind.app.auth.RegisterActivity
import com.example.suarasamind.app.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private lateinit var autoScrollRunnable: Runnable
    private val SCROLL_DELAY_MS = 4000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupButtons()
        setupAutoScroll()
        animateEntrance()
    }

    private fun setupViewPager() {
        onboardingAdapter = OnboardingAdapter(this)
        binding.onboardingViewPager.adapter = onboardingAdapter
        binding.dotsIndicator.attachTo(binding.onboardingViewPager)

        // Page transformer untuk smooth transition
        binding.onboardingViewPager.setPageTransformer { page, position ->
            page.apply {
                val absPosition = Math.abs(position)
                alpha = 1f - absPosition
                scaleX = 1f - (absPosition * 0.1f)
                scaleY = 1f - (absPosition * 0.1f)
            }
        }

        // Listener untuk perubahan halaman
        binding.onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Sembunyikan tombol skip di halaman terakhir
                binding.btnSkip.visibility = if (position == onboardingAdapter.itemCount - 1) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

                // Animasi untuk dots indicator
                binding.dotsIndicator.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(150)
                    .withEndAction {
                        binding.dotsIndicator.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .start()
                    }
                    .start()
            }
        })
    }

    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        binding.btnSkip.setOnClickListener {
            // Skip ke halaman terakhir dengan animasi smooth
            binding.onboardingViewPager.setCurrentItem(
                onboardingAdapter.itemCount - 1,
                true
            )
        }
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

    private fun animateEntrance() {
        // Animasi fade in untuk skip button
        binding.btnSkip.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(300)
                .start()
        }

        // Animasi slide up untuk dots indicator
        binding.dotsIndicator.apply {
            translationY = 50f
            alpha = 0f
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(400)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        // Animasi slide up untuk buttons
        binding.btnRegister.apply {
            translationY = 100f
            alpha = 0f
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        binding.btnLogin.apply {
            translationY = 100f
            alpha = 0f
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(600)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
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