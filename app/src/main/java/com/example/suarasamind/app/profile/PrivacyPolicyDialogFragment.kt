package com.example.suarasamind.app.profile // Sesuaikan dengan package Anda

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.suarasamind.app.R
import com.example.suarasamind.app.databinding.DialogPrivacyPolicyBinding

class PrivacyPolicyDialogFragment : DialogFragment() {

    private var _binding: DialogPrivacyPolicyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set teks kebijakan dari strings.xml dan render HTML-nya
        binding.tvPolicyContent.text = Html.fromHtml(getString(R.string.privacy_policy_html), Html.FROM_HTML_MODE_COMPACT)
        binding.tvPolicyContent.movementMethod = LinkMovementMethod.getInstance() // Agar link bisa diklik jika ada

        // Set listener untuk tombol tutup
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        // Atur ukuran dialog
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // Atur background dialog menjadi transparan agar background custom kita terlihat
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}