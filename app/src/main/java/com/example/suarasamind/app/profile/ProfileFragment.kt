package com.example.suarasamind.app.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.suarasamind.app.R
import com.example.suarasamind.app.adapters.AvatarAdapter
import com.example.suarasamind.app.auth.LoginActivity
import com.example.suarasamind.app.databinding.FragmentProfileBinding
import com.example.suarasamind.app.mood.MoodHistoryActivity

// [TAMBAHAN] Import untuk DialogFragment Kebijakan Privasi
import com.example.suarasamind.app.profile.PrivacyPolicyDialogFragment

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupClickListeners()
    }

    private fun observeViewModel() {
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUserName.text = it.fullName
                binding.tvUserEmail.text = it.email

                val avatarIdString = it.avatarId
                val placeholder = R.drawable.ic_profile
                var resourceId = placeholder

                if (avatarIdString.isNotEmpty()) {
                    val foundId = resources.getIdentifier(avatarIdString, "drawable", requireContext().packageName)
                    if (foundId != 0) {
                        resourceId = foundId
                    }
                }

                Glide.with(this)
                    .load(resourceId)
                    .placeholder(placeholder)
                    .circleCrop()
                    .into(binding.ivProfilePicture)
            }
        }
        profileViewModel.journalCount.observe(viewLifecycleOwner) { count ->
            binding.tvJournalCount.text = count.toString()
        }
        profileViewModel.moodCount.observe(viewLifecycleOwner) { count ->
            binding.tvMoodCount.text = count.toString()
        }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            profileViewModel.logout()
            val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            intent.putExtra("CURRENT_NAME", binding.tvUserName.text.toString())
            startActivity(intent)
        }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
        }

        binding.ivProfilePicture.setOnClickListener {
            showAvatarSelectionDialog()
        }

        binding.btnMoodHistory.setOnClickListener {
            startActivity(Intent(requireActivity(), MoodHistoryActivity::class.java))
        }

        binding.tvMoodCount.setOnClickListener {
            startActivity(Intent(requireActivity(), MoodHistoryActivity::class.java))
        }

        // [TAMBAHAN] Click listener untuk tombol Kebijakan Privasi
        binding.btnPrivacyPolicy.setOnClickListener {
            openPrivacyPolicy()
        }
    }

    // [TAMBAHAN] Fungsi untuk memanggil DialogFragment Kebijakan Privasi
    private fun openPrivacyPolicy() {
        PrivacyPolicyDialogFragment().show(childFragmentManager, "PrivacyPolicyDialog")
    }

    private fun showAvatarSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_select_avatar, null)
        val rvAvatars = dialogView.findViewById<RecyclerView>(R.id.rv_avatars)

        val avatars = listOf(
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6
        )

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val adapter = AvatarAdapter(avatars) { selectedAvatarId ->
            profileViewModel.updateAvatar(selectedAvatarId)
            dialog.dismiss()
        }
        rvAvatars.adapter = adapter

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}