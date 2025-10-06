package com.example.suarasamind.app.adapters // Sesuaikan dengan package Anda

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.databinding.ItemAvatarBinding

class AvatarAdapter(
    private val avatarList: List<Int>,
    private val onAvatarClick: (String) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    inner class AvatarViewHolder(private val binding: ItemAvatarBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(avatarResId: Int) {
            binding.ivAvatarItem.setImageResource(avatarResId)
            itemView.setOnClickListener {
                val resourceName = itemView.context.resources.getResourceEntryName(avatarResId)
                onAvatarClick(resourceName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = ItemAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        holder.bind(avatarList[position])
    }

    override fun getItemCount(): Int = avatarList.size
}