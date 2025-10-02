package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.ForumPost

class ForumAdapter(private val postList: List<ForumPost>) :
    RecyclerView.Adapter<ForumAdapter.ViewHolder>() {

    // Listener untuk menangani klik pada setiap item
    var onItemClick: ((ForumPost) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_post_title)
        val snippet: TextView = view.findViewById(R.id.tv_post_content_snippet)
        val author: TextView = view.findViewById(R.id.tv_author)
        val stats: TextView = view.findViewById(R.id.tv_post_stats)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(postList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_post_full, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]
        holder.title.text = post.title
        holder.snippet.text = post.content
        holder.author.text = "oleh ${post.authorUsername}"
        holder.stats.text = "${post.commentCount} Komentar • ${post.supportCount} Dukungan"
    }

    override fun getItemCount() = postList.size
}

