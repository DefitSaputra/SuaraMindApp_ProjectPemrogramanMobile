package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R

class ForumHeaderAdapter : RecyclerView.Adapter<ForumHeaderAdapter.HeaderViewHolder>() {

    var onRefreshClick: (() -> Unit)? = null

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val refreshButton: ImageView = view.findViewById(R.id.iv_refresh)
        init {
            refreshButton.setOnClickListener {
                onRefreshClick?.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_header, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 1
}