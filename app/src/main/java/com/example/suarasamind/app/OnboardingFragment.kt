package com.example.suarasamind.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class OnboardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)
        val imageRes = requireArguments().getInt(ARG_IMAGE)
        view.findViewById<ImageView>(R.id.onboarding_image).setImageResource(imageRes)
        return view
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