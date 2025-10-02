package com.example.suarasamind.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.CreatePostActivity
import com.example.suarasamind.app.PostDetailActivity
import com.example.suarasamind.app.R
import com.example.suarasamind.app.adapters.ForumAdapter
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.databinding.FragmentForumBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var forumAdapter: ForumAdapter
    private val postList = mutableListOf<ForumPost>()

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

        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        listenToForumPosts()

        binding.fabCreatePost.setOnClickListener {
            // Navigate to CreatePostActivity
            val intent = Intent(requireActivity(), CreatePostActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        forumAdapter = ForumAdapter(postList)
        binding.rvForumPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forumAdapter
        }

        // Menangani klik pada item postingan
        forumAdapter.onItemClick = { post ->
            // Navigate to PostDetailActivity sementara
            val intent = Intent(requireActivity(), PostDetailActivity::class.java)
            intent.putExtra("POST_ID", post.id)
            startActivity(intent)
        }
    }

    private fun listenToForumPosts() {
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    postList.clear()
                    for (document in snapshots) {
                        val post = document.toObject(ForumPost::class.java)
                        post.id = document.id
                        postList.add(post)
                    }
                    forumAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}