// File: adapters/CalendarAdapter.kt
package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.CalendarDay
import com.example.suarasamind.app.databinding.ItemCalendarDayBinding
import java.util.Date

class CalendarAdapter : ListAdapter<CalendarDay, CalendarAdapter.ViewHolder>(DiffCallback()) {

    var onDateClick: ((Date) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: CalendarDay) {
            if (day.dayNumber == 0) {
                // Hari kosong untuk alignment
                binding.root.visibility = View.INVISIBLE
                binding.root.isClickable = false
            } else {
                binding.root.visibility = View.VISIBLE
                binding.tvDayNumber.text = day.dayNumber.toString()
                binding.root.isClickable = true

                // Tampilkan ikon mood jika ada
                if (day.moodType != null) {
                    binding.ivMoodIcon.visibility = View.VISIBLE
                    val moodIcon = when (day.moodType) {
                        "happy" -> R.drawable.emo_happy
                        "sad" -> R.drawable.emo_sad
                        "angry" -> R.drawable.emo_angry
                        "flat" -> R.drawable.emo_flat
                        else -> 0 // Sebaiknya ada ikon default
                    }
                    if (moodIcon != 0) binding.ivMoodIcon.setImageResource(moodIcon)
                } else {
                    binding.ivMoodIcon.visibility = View.GONE
                }

                // Terapkan style berdasarkan properti isSelected dari data
                if (day.isSelected) {
                    binding.cardDay.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.calm_blue))
                    binding.tvDayNumber.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                } else {
                    binding.cardDay.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
                    binding.tvDayNumber.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_dark))
                }

                // Klik listener hanya memanggil callback, tidak mengubah state di adapter
                binding.root.setOnClickListener {
                    day.date?.let { date ->
                        onDateClick?.invoke(date)
                    }
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            // Kita perlu ID unik, tapi date bisa jadi null. Kombinasi dayNumber dan date cukup baik.
            return oldItem.dayNumber == newItem.dayNumber && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem == newItem
        }
    }
}