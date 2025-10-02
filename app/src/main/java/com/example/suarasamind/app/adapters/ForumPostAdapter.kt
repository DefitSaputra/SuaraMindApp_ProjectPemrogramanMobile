package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.ForumPostData

class ForumPostAdapter(private val postList: List<ForumPostData>) :
    RecyclerView.Adapter<ForumPostAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val postTitle: TextView = view.findViewById(R.id.tv_post_title)
        val postDetails: TextView = view.findViewById(R.id.tv_post_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = postList[position]
        holder.postTitle.text = item.title
        holder.postDetails.text = "${item.commentCount} Komentar • ${item.supportCount} Dukungan"
    }

    override fun getItemCount() = postList.size
}
