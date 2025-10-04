package com.example.suarasamind.app.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.R
import com.example.suarasamind.app.adapters.ContentAdapter
import com.example.suarasamind.app.adapters.ForumAdapter
import com.example.suarasamind.app.adapters.MoodAdapter
import com.example.suarasamind.app.auth.LoginActivity
import com.example.suarasamind.app.data.MoodData
import com.example.suarasamind.app.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var moodAdapter: MoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()

        binding.ivMenu.setOnClickListener { anchorView ->
            showLogoutMenu(anchorView)
        }
    }

    private fun setupUI() {
        setupMoodTracker()
        binding.rvArticles.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvForum.layoutManager = LinearLayoutManager(requireContext())
        binding.rvForum.isNestedScrollingEnabled = false

        val todayDate = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID")).format(Date())
        binding.tvDate.text = todayDate
    }

    private fun observeViewModel() {
        homeViewModel.greeting.observe(viewLifecycleOwner) { greetingText ->
            binding.tvGreeting.text = greetingText
        }

        homeViewModel.articles.observe(viewLifecycleOwner) { articles ->
            val contentAdapter = ContentAdapter(articles)
            binding.rvArticles.adapter = contentAdapter
            contentAdapter.onItemClick = { article ->
                Toast.makeText(requireContext(), "Membuka artikel: ${article.title}", Toast.LENGTH_SHORT).show()
            }
        }

        homeViewModel.forumPosts.observe(viewLifecycleOwner) { posts ->
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val forumAdapter = ForumAdapter(posts, currentUserId)
            binding.rvForum.adapter = forumAdapter

            forumAdapter.onItemClick = {
                val navController = Navigation.findNavController(requireView())
                navController.navigate(R.id.action_homeFragment_to_forumFragment)
            }
            forumAdapter.onSupportClick = { post ->
                homeViewModel.toggleSupport(post)
            }
        }
    }

    private fun setupMoodTracker() {
        // Daftar mood dengan emoji vector drawable yang sudah Anda miliki
        val moodList = listOf(
            MoodData(
                iconResId = R.drawable.emo_sad,
                borderColorResId = R.color.mood_sad_border,
                bgColorResId = R.color.mood_sad_bg,
                message = "Tidak apa-apa, Suarasa Mind ada untukmu. 😊",
                type = "sad"
            ),
            MoodData(
                iconResId = R.drawable.emo_angry,
                borderColorResId = R.color.mood_angry_border,
                bgColorResId = R.color.mood_angry_bg,
                message = "Wah, sepertinya butuh sedikit ketenangan. Kami di sini!",
                type = "angry"
            ),
            MoodData(
                iconResId = R.drawable.emo_flat,
                borderColorResId = R.color.mood_flat_border,
                bgColorResId = R.color.mood_flat_bg,
                message = "Kadang begini, mari cari inspirasi bersama! ✨",
                type = "flat"
            ),
            MoodData(
                iconResId = R.drawable.emo_happy,
                borderColorResId = R.color.mood_happy_border,
                bgColorResId = R.color.mood_happy_bg,
                message = "Senyummu menular! Tetap semangat ya! 🎉",
                type = "happy"
            )
        )

        moodAdapter = MoodAdapter(moodList)
        binding.rvMoodTracker.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = moodAdapter
        }

        moodAdapter.onItemClick = { mood ->
            Toast.makeText(requireContext(), mood.message, Toast.LENGTH_SHORT).show()

            // Simpan mood ke Firebase
            homeViewModel.saveMood(mood.type)
        }
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