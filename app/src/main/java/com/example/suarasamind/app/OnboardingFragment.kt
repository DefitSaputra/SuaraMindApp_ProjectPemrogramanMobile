package com.example.suarasamind.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView

class OnboardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)
        val imageRes = requireArguments().getInt(ARG_IMAGE)
        val imageView = view.findViewById<ImageView>(R.id.onboarding_image)

        imageView.setImageResource(imageRes)

        // Animasi smooth fade in dan scale untuk ilustrasi
        animateIllustration(imageView)

        return view
    }

    private fun animateIllustration(imageView: ImageView) {
        imageView.apply {
            alpha = 0f
            scaleX = 0.9f
            scaleY = 0.9f

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(700)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    companion object {
        private const val ARG_IMAGE = "arg_image"

        fun newInstance(imageRes: Int): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle().apply {
                putInt(ARG_IMAGE, imageRes)
            }
            fragment.arguments = args
            return fragment
        }
    }
}