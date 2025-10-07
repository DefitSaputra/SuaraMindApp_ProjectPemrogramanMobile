package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.ForumPost
import com.example.suarasamind.app.databinding.ItemForumPostFullBinding

class ForumAdapter(private var postList: MutableList<ForumPost>, private val currentUserId: String) :
    RecyclerView.Adapter<ForumAdapter.ViewHolder>() {

    var onItemClick: ((ForumPost) -> Unit)? = null
    var onSupportClick: ((ForumPost) -> Unit)? = null

    fun updatePosts(newPosts: List<ForumPost>) {
        postList.clear()
        postList.addAll(newPosts)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemForumPostFullBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(postList[position])
                }
            }
            binding.btnSupport.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSupportClick?.invoke(postList[position])
                }
            }
        }

        fun bind(post: ForumPost) {
            binding.tvPostTitle.text = post.title
            binding.tvPostContentSnippet.text = post.content
            binding.tvAuthor.text = "oleh ${post.authorUsername}"

            binding.btnSupport.text = "${post.supportCount} Dukung"
            binding.btnComment.text = "${post.commentCount} Komentar"

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemForumPostFullBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount() = postList.size
}