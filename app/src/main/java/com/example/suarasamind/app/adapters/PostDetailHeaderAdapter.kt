package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.databinding.ItemPostDetailHeaderBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PostDetailHeaderAdapter(
    private val currentUserId: String
) : RecyclerView.Adapter<PostDetailHeaderAdapter.HeaderViewHolder>() {

    private var post: ForumPost? = null
    var onSupportClick: (() -> Unit)? = null

    fun setPost(forumPost: ForumPost) {
        post = forumPost
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val binding = ItemPostDetailHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        post?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = if (post == null) 0 else 1

    inner class HeaderViewHolder(private val binding: ItemPostDetailHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: ForumPost) {
            binding.tvPostTitle.text = post.title
            binding.tvPostAuthor.text = "oleh ${post.authorUsername}"
            binding.tvPostContent.text = post.content
            binding.tvSupportCount.text = post.supportCount.toString()
            binding.tvCommentCount.text = post.commentCount.toString()

            post.timestamp?.let { timestamp ->
                val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                binding.tvPostDate.text = "• ${sdf.format(timestamp)}"
            }

            // Atur tampilan tombol support/like
            if (post.supporters.contains(currentUserId)) {
                // User sudah like
                binding.btnSupport.setIconResource(R.drawable.ic_favorite_filled)
                // PERUBAHAN: Gunakan calm_blue agar konsisten
                binding.btnSupport.iconTint = ContextCompat.getColorStateList(itemView.context, R.color.calm_blue)
                binding.btnSupport.setTextColor(ContextCompat.getColor(itemView.context, R.color.calm_blue))
            } else {
                // User belum like
                binding.btnSupport.setIconResource(R.drawable.ic_favorite_border)
                binding.btnSupport.iconTint = ContextCompat.getColorStateList(itemView.context, R.color.text_light)
                binding.btnSupport.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_light))
            }

            // Set listener untuk tombol support
            binding.btnSupport.setOnClickListener {
                onSupportClick?.invoke()
            }
        }
    }
}