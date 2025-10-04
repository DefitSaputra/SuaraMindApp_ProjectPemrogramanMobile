package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.JournalEntry
import com.example.suarasamind.app.databinding.ItemJournalEntryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class JournalAdapter(private val journalList: List<JournalEntry>) :
    RecyclerView.Adapter<JournalAdapter.ViewHolder>() {

    var onItemClick: ((JournalEntry) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemJournalEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(journalList[adapterPosition])
                }
            }
        }

        fun bind(entry: JournalEntry) {
            binding.tvJournalTitle.text = entry.title
            binding.tvJournalSnippet.text = entry.content

            // Atur ikon mood
            val moodIcon = when (entry.mood) {
                "happy" -> R.drawable.emo_happy
                "sad" -> R.drawable.emo_sad
                "angry" -> R.drawable.emo_angry
                else -> R.drawable.emo_flat
            }
            binding.ivMoodIndicator.setImageResource(moodIcon)

            // Format dan tampilkan tanggal
            entry.timestamp?.let { date ->
                binding.tvDayOfWeek.text = SimpleDateFormat("EEEE", Locale("id", "ID")).format(date).uppercase()
                binding.tvDay.text = SimpleDateFormat("dd", Locale.getDefault()).format(date)
                binding.tvMonthYear.text = SimpleDateFormat("MMM yyyy", Locale("id", "ID")).format(date).uppercase()
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
        holder.bind(journalList[position])
    }

    override fun getItemCount() = journalList.size
}