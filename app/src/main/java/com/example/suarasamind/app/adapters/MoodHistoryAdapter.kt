// File: adapters/MoodHistoryAdapter.kt
package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.MoodEntry
import com.example.suarasamind.app.databinding.ItemMoodHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class MoodHistoryAdapter : ListAdapter<MoodEntry, MoodHistoryAdapter.ViewHolder>(DiffCallback()) {

    private data class MoodVisuals(
        val name: String,
        @DrawableRes val icon: Int,
        @ColorRes val backgroundColor: Int,
        val message: String
    )

    private fun getMoodVisuals(moodType: String): MoodVisuals {
        return when (moodType) {
            "happy" -> MoodVisuals("Senang", R.drawable.emo_happy, R.color.mood_happy_bg, "Hari yang menyenangkan! 🎉")
            "sad" -> MoodVisuals("Sedih", R.drawable.emo_sad, R.color.mood_sad_bg, "Tidak apa-apa, akan lebih baik 💙")
            "angry" -> MoodVisuals("Marah", R.drawable.emo_angry, R.color.mood_angry_bg, "Tetap tenang, ini akan berlalu 🌊")
            "flat" -> MoodVisuals("Biasa Saja", R.drawable.emo_flat, R.color.mood_flat_bg, "Hari yang biasa saja ✨")
            else -> MoodVisuals("Tidak Diketahui", R.drawable.emo_flat, R.color.mood_flat_bg, "")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMoodHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemMoodHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mood: MoodEntry) {
            val visuals = getMoodVisuals(mood.type)

            binding.ivMoodIcon.setImageResource(visuals.icon)
            binding.tvMoodName.text = visuals.name
            binding.tvMessage.text = visuals.message
            binding.cardMood.setCardBackgroundColor(
                ContextCompat.getColor(itemView.context, visuals.backgroundColor)
            )

            mood.timestamp?.let { timestamp ->
                val sdf = SimpleDateFormat("HH:mm", Locale("id", "ID"))
                binding.tvTime.text = sdf.format(timestamp)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MoodEntry>() {
        override fun areItemsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean = oldItem == newItem
    }
}