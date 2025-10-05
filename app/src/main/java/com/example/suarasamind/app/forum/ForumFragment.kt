// Isi file ForumFragment.kt (YANG BENAR)

package com.example.suarasamind.app.forum

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.adapters.ForumAdapter
import com.example.suarasamind.app.adapters.ForumHeaderAdapter
import com.example.suarasamind.app.databinding.FragmentForumBinding
import com.google.firebase.auth.FirebaseAuth

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    private val forumViewModel: ForumViewModel by viewModels()
    // Deklarasi untuk semua adapter
    private lateinit var headerAdapter: ForumHeaderAdapter
    private lateinit var forumAdapter: ForumAdapter
    private lateinit var concatAdapter: ConcatAdapter

    private var isInitialLoad = true

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
        setupRecyclerView()
        observeViewModel()

        binding.fabCreatePost.setOnClickListener {
            val intent = Intent(requireActivity(), CreatePostActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Inisialisasi setiap adapter
        headerAdapter = ForumHeaderAdapter()
        forumAdapter = ForumAdapter(mutableListOf(), currentUserId)

        // Gabungkan keduanya
        concatAdapter = ConcatAdapter(headerAdapter, forumAdapter)

        binding.rvForumPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        // Setup listener untuk klik
        forumAdapter.onItemClick = { post ->
            val intent = Intent(requireActivity(), PostDetailActivity::class.java)
            intent.putExtra("POST_ID", post.id)
            startActivity(intent)
        }

        forumAdapter.onSupportClick = { post ->
            forumViewModel.toggleSupport(post)
        }

        // Listener untuk tombol refresh di header
        headerAdapter.onRefreshClick = {
            binding.progressBar.isVisible = true // Tampilkan loading saat refresh
            forumViewModel.refreshPosts() // Panggil fungsi refresh di ViewModel
        }
    }

    private fun observeViewModel() {
        if (isInitialLoad) {
            binding.progressBar.isVisible = true
            binding.layoutEmptyState.isVisible = false
            binding.rvForumPosts.isVisible = false
        }

        forumViewModel.posts.observe(viewLifecycleOwner) { postList ->
            binding.progressBar.isVisible = false
            isInitialLoad = false

            if (postList.isEmpty()) {
                binding.rvForumPosts.isVisible = false
                binding.layoutEmptyState.isVisible = true
            } else {
                binding.rvForumPosts.isVisible = true
                binding.layoutEmptyState.isVisible = false
                forumAdapter.updatePosts(postList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
