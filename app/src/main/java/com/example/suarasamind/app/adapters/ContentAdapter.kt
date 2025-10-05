package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.ContentData

class ContentAdapter(private val contentList: List<ContentData>) :
    RecyclerView.Adapter<ContentAdapter.ViewHolder>() {
    var onItemClick: ((ContentData) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentImage: ImageView = view.findViewById(R.id.iv_content_image)
        val contentTitle: TextView = view.findViewById(R.id.tv_content_title)
        val contentSnippet: TextView = view.findViewById(R.id.tv_content_snippet)

        init {
            // Set click listener untuk seluruh item
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(contentList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_content_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = contentList[position]

        holder.contentTitle.text = item.title
        holder.contentSnippet.text = item.content

        // Load gambar menggunakan Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_article_placeholder)
            .error(R.drawable.ic_article_placeholder)
            .centerCrop()
            .into(holder.contentImage)
    }

    override fun getItemCount() = contentList.size
}