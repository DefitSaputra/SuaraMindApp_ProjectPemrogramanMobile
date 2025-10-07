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

    private val forumViewModel: ForumViewModel by viewModels()
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
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        forumAdapter = ForumAdapter(mutableListOf(), currentUserId)

        binding.rvForumPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forumAdapter
        }

        forumAdapter.onItemClick = { post ->
            val intent = Intent(requireActivity(), PostDetailActivity::class.java)
            intent.putExtra("POST_ID", post.id)
            startActivity(intent)
        }

        forumAdapter.onSupportClick = { post ->
            forumViewModel.toggleSupport(post)
        }
    }

    private fun observeViewModel() {
        forumViewModel.posts.observe(viewLifecycleOwner) { postList ->
            forumAdapter.updatePosts(postList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}