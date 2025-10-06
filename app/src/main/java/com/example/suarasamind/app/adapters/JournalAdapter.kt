package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.JournalEntry
import com.example.suarasamind.app.databinding.ItemJournalEntryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// PERUBAHAN 1: Menggunakan ListAdapter, tidak lagi perlu `private val journalList` di constructor
class JournalAdapter : ListAdapter<JournalEntry, JournalAdapter.ViewHolder>(JournalDiffCallback()) {

    var onItemClick: ((JournalEntry) -> Unit)? = null

    // companion object untuk menyimpan formatter agar tidak dibuat berulang kali
    companion object {
        // PERUBAHAN 2: Optimasi SimpleDateFormat
        private val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("id", "ID"))
        private val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        private val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale("id", "ID"))
    }

    inner class ViewHolder(private val binding: ItemJournalEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                // Gunakan getItem(adapterPosition) untuk mendapatkan item yang benar
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(getItem(adapterPosition))
                }
            }
        }

        fun bind(entry: JournalEntry) {
            binding.tvJournalTitle.text = entry.title
            binding.tvJournalSnippet.text = entry.content

            val moodIcon = when (entry.mood) {
                "happy" -> R.drawable.emo_happy
                "sad" -> R.drawable.emo_sad
                "angry" -> R.drawable.emo_angry
                else -> R.drawable.emo_flat
            }
            binding.ivMoodIndicator.setImageResource(moodIcon)

            // Gunakan formatter yang sudah dibuat di companion object
            entry.timestamp?.let { date ->
                binding.tvDayOfWeek.text = dayOfWeekFormat.format(date).uppercase()
                binding.tvDay.text = dayFormat.format(date)
                binding.tvMonthYear.text = monthYearFormat.format(date).uppercase()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemJournalEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Gunakan getItem(position) untuk mendapatkan data
        holder.bind(getItem(position))
    }

    // Kelas DiffUtil untuk memberitahu ListAdapter cara membandingkan item
    class JournalDiffCallback : DiffUtil.ItemCallback<JournalEntry>() {
        override fun areItemsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean {
            // Bandingkan berdasarkan ID unik
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean {
            // Bandingkan isi datanya, data class otomatis membuat method equals() yang cocok untuk ini
            return oldItem == newItem
        }
    }
}