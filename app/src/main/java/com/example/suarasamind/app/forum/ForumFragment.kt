package com.example.suarasamind.app.forum

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.adapters.ForumAdapter
import com.example.suarasamind.app.databinding.FragmentForumBinding
import com.google.firebase.auth.FirebaseAuth

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi ViewModel dengan cara yang benar
    private val forumViewModel: ForumViewModel by viewModels()

    // Adapter akan diinisialisasi nanti saat ada data
    private lateinit var forumAdapter: ForumAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()

        binding.fabCreatePost.setOnClickListener {
            val intent = Intent(requireActivity(), CreatePostActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupUI() {
        // Buat adapter dengan list kosong terlebih dahulu untuk setup awal
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        forumAdapter = ForumAdapter(emptyList(), currentUserId)

        binding.rvForumPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forumAdapter
        }
    }

    private fun observeViewModel() {
        // Mengamati perubahan pada daftar post dari ViewModel
        forumViewModel.posts.observe(viewLifecycleOwner) { postList ->
            // Saat data baru datang dari ViewModel, buat ulang adapter dengan data baru
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            forumAdapter = ForumAdapter(postList, currentUserId)
            binding.rvForumPosts.adapter = forumAdapter

            // Atur listener untuk adapter yang baru
            forumAdapter.onItemClick = { post ->
                val intent = Intent(requireActivity(), PostDetailActivity::class.java)
                intent.putExtra("POST_ID", post.id)
                startActivity(intent)
            }

            forumAdapter.onSupportClick = { post ->
                // Perintahkan ViewModel untuk menangani logika 'like'
                forumViewModel.toggleSupport(post)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}