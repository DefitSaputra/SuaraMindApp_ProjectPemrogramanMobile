package com.example.suarasamind.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.R
import com.example.suarasamind.app.adapters.ContentAdapter
import com.example.suarasamind.app.adapters.ForumAdapter
import com.example.suarasamind.app.adapters.MoodAdapter
import com.example.suarasamind.app.data.ContentData
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.data.MoodData
import com.example.suarasamind.app.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var moodAdapter: MoodAdapter
    private val moodList = mutableListOf<MoodData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        setupUI()
        loadUserProfile()
    }

    private fun setupUI() {
        setupMoodTracker()
        setupRecyclerViews()
    }

    private fun setupMoodTracker() {
        moodList.addAll(listOf(
            MoodData(R.drawable.emo_sad, R.color.mood_sad_border, R.color.mood_sad_bg, "Tidak apa-apa, Suarasa Mind ada untukmu. 😊", "sad"),
            MoodData(R.drawable.emo_angry, R.color.mood_angry_border, R.color.mood_angry_bg, "Wah, sepertinya butuh sedikit ketenangan. Kami di sini!", "angry"),
            MoodData(R.drawable.emo_flat, R.color.mood_flat_border, R.color.mood_flat_bg, "Kadang begini, mari cari inspirasi bersama! ✨", "flat"),
            MoodData(R.drawable.emo_happy, R.color.mood_happy_border, R.color.mood_happy_bg, "Senyummu menular! Tetap semangat ya! 🎉", "happy")
        ))

        moodAdapter = MoodAdapter(moodList)

        binding.rvMoodTracker.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = moodAdapter
        }

        moodAdapter.onItemClick = { mood ->
            // Show mood message
            Toast.makeText(requireContext(), mood.message, Toast.LENGTH_SHORT).show()
            saveMoodToFirestore(mood.type)
        }
    }

    private fun saveMoodToFirestore(moodType: String) {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val moodData = hashMapOf(
                "type" to moodType,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("users").document(userId)
                .collection("moods")
                .add(moodData)
                .addOnSuccessListener {
                    // Mood saved successfully
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
        }
    }

    private fun loadUserProfile() {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val fullName = document.getString("fullName") ?: "Sobat"

                    // Update greeting berdasarkan waktu
                    val hour = java.text.SimpleDateFormat("HH", java.util.Locale.getDefault()).format(java.util.Date()).toInt()
                    val greetingPrefix = when (hour) {
                        in 5..11 -> "Selamat Pagi,"
                        in 12..15 -> "Selamat Siang,"
                        in 16..18 -> "Selamat Sore,"
                        else -> "Selamat Malam,"
                    }

                    val greeting = "$greetingPrefix\n$fullName!"
                    binding.tvGreeting.text = greeting
                }
                .addOnFailureListener { e ->
                    binding.tvGreeting.text = "Selamat Pagi,\nSobat!"
                }
        }
    }

    private fun setupRecyclerViews() {
        // Load articles dari Firestore
        firestore.collection("articles")
            .get()
            .addOnSuccessListener { result ->
                val articles = result.map { document ->
                    ContentData(
                        title = document.getString("title") ?: "",
                        content = document.getString("content") ?: "",
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                }
                binding.rvArticles.adapter = ContentAdapter(articles)
            }
            .addOnFailureListener { e ->
                // Jika gagal load, gunakan data dummy
                val dummyArticles = listOf(
                    ContentData("Tips Jitu Mengelola Stres Kerja", "", ""),
                    ContentData("Meditasi Singkat untuk Fokus", "", ""),
                    ContentData("Pentingnya Self-Care untukmu", "", "")
                )
                binding.rvArticles.adapter = ContentAdapter(dummyArticles)
            }

        // Load forum posts dari Firestore
        firestore.collection("posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(3) // Hanya ambil 3 post terbaru untuk home
            .get()
            .addOnSuccessListener { result ->
                val forumPosts = result.map { document ->
                    val post = document.toObject(ForumPost::class.java)
                    post.id = document.id
                    post
                }

                val forumAdapter = ForumAdapter(forumPosts)
                binding.rvForum.adapter = forumAdapter
                binding.rvForum.isNestedScrollingEnabled = false

                // Tambahkan listener untuk navigasi ke ForumActivity
                forumAdapter.onItemClick = { post ->
                    // Navigasi ke ForumFragment
                    val navController = androidx.navigation.Navigation.findNavController(requireView())
                    navController.navigate(R.id.action_homeFragment_to_forumFragment)
                }
            }
            .addOnFailureListener { e ->
                // Jika gagal load, gunakan data dummy
                val dummyForumPosts = listOf(
                    ForumPost(
                        title = "Apakah wajar merasa cemas sebelum presentasi?",
                        authorUsername = "Anonim",
                        content = "",
                        commentCount = 5,
                        supportCount = 12
                    ),
                    ForumPost(
                        title = "Bagaimana cara membangun kebiasaan baik?",
                        authorUsername = "Pengguna",
                        content = "",
                        commentCount = 8,
                        supportCount = 25
                    ),
                    ForumPost(
                        title = "Rekomendasi buku self-improvement",
                        authorUsername = "BookLover",
                        content = "",
                        commentCount = 11,
                        supportCount = 40
                    )
                )

                val forumAdapter = ForumAdapter(dummyForumPosts)
                binding.rvForum.adapter = forumAdapter
                binding.rvForum.isNestedScrollingEnabled = false

                // Tambahkan listener untuk navigasi ke ForumActivity
                forumAdapter.onItemClick = { post ->
                    // Navigasi ke ForumFragment
                    val navController = androidx.navigation.Navigation.findNavController(requireView())
                    navController.navigate(R.id.action_homeFragment_to_forumFragment)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}