package com.example.suarasamind.app

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment.newInstance(R.drawable.illustration_page1)
            1 -> OnboardingFragment.newInstance(R.drawable.illustration_page2)
            2 -> OnboardingFragment.newInstance(R.drawable.illustration_page3)
            else -> Fragment() // Fallback
        }
    }
}