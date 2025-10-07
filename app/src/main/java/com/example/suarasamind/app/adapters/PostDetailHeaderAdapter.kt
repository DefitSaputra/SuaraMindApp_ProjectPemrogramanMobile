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

        init {
            binding.btnSupport.setOnClickListener {
                onSupportClick?.invoke()
            }
        }

        fun bind(post: ForumPost) {
            binding.tvPostTitle.text = post.title
            binding.tvPostAuthor.text = "oleh ${post.authorUsername}"
            binding.tvPostContent.text = post.content
            binding.btnSupport.text = "${post.supportCount} Dukung"
            binding.tvCommentCount.text = "${post.commentCount} Komentar"

            post.timestamp?.let { timestamp ->
                val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                binding.tvPostDate.text = "• ${sdf.format(timestamp)}"
            }

            if (post.supporters.contains(currentUserId)) {
                binding.btnSupport.setIconResource(R.drawable.ic_favorite_filled)
                binding.btnSupport.iconTint = ContextCompat.getColorStateList(itemView.context, R.color.calm_blue)
                binding.btnSupport.setTextColor(ContextCompat.getColor(itemView.context, R.color.calm_blue))
            } else {
                binding.btnSupport.setIconResource(R.drawable.ic_favorite_border)
                binding.btnSupport.iconTint = ContextCompat.getColorStateList(itemView.context, R.color.text_light)
                binding.btnSupport.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_light))
            }
        }
    }
}