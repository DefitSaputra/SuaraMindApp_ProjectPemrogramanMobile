package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.Comment
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(private val commentList: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.iv_comment_avatar)
        val author: TextView = view.findViewById(R.id.tv_comment_author)
        val content: TextView = view.findViewById(R.id.tv_comment_content)
        val date: TextView = view.findViewById(R.id.tv_comment_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = commentList[position]

        holder.author.text = comment.authorUsername
        holder.content.text = comment.content

        // Format tanggal
        comment.timestamp?.let { timestamp ->
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            holder.date.text = sdf.format(timestamp)
        }

    }

    override fun getItemCount() = commentList.size
}