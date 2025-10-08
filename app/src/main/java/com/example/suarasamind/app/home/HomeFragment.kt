package com.example.suarasamind.app.home

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.R
import com.example.suarasamind.app.adapters.ContentAdapter
import com.example.suarasamind.app.adapters.ForumAdapter
import com.example.suarasamind.app.adapters.MoodAdapter
import com.example.suarasamind.app.auth.LoginActivity
import com.example.suarasamind.app.data.MoodData
import com.example.suarasamind.app.databinding.FragmentHomeBinding
import com.example.suarasamind.app.main.MainActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var moodAdapter: MoodAdapter
    private lateinit var forumAdapter: ForumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupUI() {
        setupMoodTracker()
        binding.rvArticles.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // 🔹 Inisialisasi ForumAdapter dengan listener langsung
        forumAdapter = ForumAdapter(
            currentUserId = currentUserId,
            onSupportClick = { post ->
                homeViewModel.toggleSupport(post)
            },
            onItemClick = {
                navigateToBottomNavItem(R.id.forumFragment)
            }
        )

        binding.rvForum.layoutManager = LinearLayoutManager(requireContext())
        binding.rvForum.isNestedScrollingEnabled = false
        binding.rvForum.adapter = forumAdapter

        val todayDate = SimpleDateFormat("E, dd MMM yyyy", Locale("id", "ID")).format(Date())
        binding.tvDate.text = todayDate
    }

    private fun setupClickListeners() {
        // 🔹 Buka navigation drawer
        binding.ivMenu.setOnClickListener {
            (activity as? MainActivity)?.binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }

        // 🔹 Buka DatePicker
        binding.btnCalendar.setOnClickListener {
            showDatePickerDialog()
        }

        // 🔹 Notifikasi
        binding.ivNotification.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }

        // 🔹 Lihat semua Forum
        binding.btnSeeAllForum.setOnClickListener {
            navigateToBottomNavItem(R.id.forumFragment)
        }
    }

    private fun observeViewModel() {
        homeViewModel.greeting.observe(viewLifecycleOwner) { greetingText ->
            binding.tvGreeting.text = greetingText
        }

        homeViewModel.articles.observe(viewLifecycleOwner) { articles ->
            val contentAdapter = ContentAdapter(articles)
            binding.rvArticles.adapter = contentAdapter

            contentAdapter.onItemClick = { article ->
                if (article.articleUrl.isNotEmpty()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.articleUrl))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Tidak bisa membuka link", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Link artikel tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 🔹 Gunakan submitList alih-alih updatePosts
        homeViewModel.forumPosts.observe(viewLifecycleOwner) { posts ->
            forumAdapter.submitList(posts)
        }

        homeViewModel.hasMoodToday.observe(viewLifecycleOwner) { hasMood ->
            if (hasMood) {
                moodAdapter.setEnabled(false)
                binding.tvMoodPrompt.text = "Kamu sudah input mood hari ini ✓"
            } else {
                moodAdapter.setEnabled(true)
                binding.tvMoodPrompt.text = "Bagaimana Perasaanmu?"
            }
        }
    }

    private fun setupMoodTracker() {
        val moodList = listOf(
            MoodData(R.drawable.emo_sad, R.color.mood_sad_border, R.color.mood_sad_bg, "Tidak apa-apa, Suarasa Mind ada untukmu 😊", "sad"),
            MoodData(R.drawable.emo_angry, R.color.mood_angry_border, R.color.mood_angry_bg, "Wah, sepertinya butuh sedikit ketenangan. Kami di sini!", "angry"),
            MoodData(R.drawable.emo_flat, R.color.mood_flat_border, R.color.mood_flat_bg, "Kadang begini, mari cari inspirasi bersama! ✨", "flat"),
            MoodData(R.drawable.emo_happy, R.color.mood_happy_border, R.color.mood_happy_bg, "Senyummu menular! Tetap semangat ya! 🎉", "happy")
        )

        moodAdapter = MoodAdapter(moodList)
        binding.rvMoodTracker.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = moodAdapter
        }

        moodAdapter.onItemClick = { mood ->
            Toast.makeText(requireContext(), mood.message, Toast.LENGTH_SHORT).show()
            homeViewModel.saveMood(mood.type)
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, _, _, _ -> },
            year, month, day
        )
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Tutup") { dialog, _ ->
            dialog.dismiss()
        }
        datePickerDialog.show()
    }

    private fun navigateToBottomNavItem(itemId: Int) {
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav.selectedItemId = itemId
    }

    private fun showLogoutMenu(anchorView: View) {
        val popupMenu = PopupMenu(requireContext(), anchorView)
        popupMenu.menuInflater.inflate(R.menu.home_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    requireActivity().finish()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
