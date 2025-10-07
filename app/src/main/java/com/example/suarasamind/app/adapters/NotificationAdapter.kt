package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.NotificationItem
import com.example.suarasamind.app.databinding.ItemNotificationBinding
import java.util.concurrent.TimeUnit

class NotificationAdapter(private val onItemClick: (NotificationItem) -> Unit) :
    ListAdapter<NotificationItem, NotificationAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationItem) {
            binding.tvNotificationMessage.text = notification.message
            binding.tvNotificationTime.text = getRelativeTime(notification.timestamp?.time ?: 0)

            // Set ikon berdasarkan tipe notifikasi
            val iconRes = when (notification.type) {
                "SUPPORT" -> R.drawable.ic_support
                "COMMENT" -> R.drawable.ic_comment
                else -> R.drawable.ic_notification
            }
            binding.ivNotificationIcon.setImageResource(iconRes)

            if (!notification.isRead) {
                binding.root.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.unread_bg_color))
                binding.viewUnreadIndicator.visibility = View.VISIBLE
            } else {
                binding.root.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.transparent))
                binding.viewUnreadIndicator.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onItemClick(notification)
            }
        }
    }

    private fun getRelativeTime(time: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - time
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        if (minutes < 60) return "$minutes menit yang lalu"
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        if (hours < 24) return "$hours jam yang lalu"
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        return "$days hari yang lalu"
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationItem>() {
        override fun areItemsTheSame(oldItem: NotificationItem, newItem: NotificationItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: NotificationItem, newItem: NotificationItem) = oldItem == newItem
    }
}