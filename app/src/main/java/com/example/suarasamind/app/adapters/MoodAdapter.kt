package com.example.suarasamind.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.MoodData
import com.google.android.material.card.MaterialCardView

class MoodAdapter(private val moodList: List<MoodData>) :
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    private var selectedPosition = -1
    private var isEnabled = true
    var onItemClick: ((MoodData) -> Unit)? = null

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        notifyDataSetChanged()
    }

    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardMood: MaterialCardView = itemView.findViewById(R.id.card_mood)
        val ivMood: ImageView = itemView.findViewById(R.id.iv_mood)
        val viewMoodBorder: View = itemView.findViewById(R.id.view_mood_border)
        val tvMoodLabel: TextView = itemView.findViewById(R.id.tv_mood_label)
        val ivMoodCheck: ImageView = itemView.findViewById(R.id.iv_mood_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_tracker, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moodList[position]
        val context = holder.itemView.context

        // Set icon dan label
        holder.ivMood.setImageResource(mood.iconResId)
        holder.tvMoodLabel.text = when (mood.type) {
            "sad" -> "Sedih"
            "angry" -> "Marah"
            "flat" -> "Datar"
            "happy" -> "Senang"
            else -> "Mood"
        }

        // Status item terpilih
        val isSelected = position == selectedPosition
        if (isSelected) {
            holder.ivMoodCheck.visibility = View.VISIBLE
            holder.cardMood.cardElevation = 8f
            holder.cardMood.strokeWidth = 3
            holder.cardMood.strokeColor = ContextCompat.getColor(context, mood.borderColorResId)
            holder.ivMood.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
            holder.ivMood.alpha = 1.0f
        } else {
            holder.ivMoodCheck.visibility = View.GONE
            holder.cardMood.cardElevation = 4f
            holder.cardMood.strokeWidth = 0
            holder.ivMood.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            holder.ivMood.alpha = 0.85f
        }

        // 🔒 Atur transparansi dan interaksi sesuai status enable/disable
        holder.itemView.alpha = if (isEnabled) 1.0f else 0.5f
        holder.cardMood.isClickable = isEnabled
        holder.cardMood.isFocusable = isEnabled

        // Klik item hanya jika aktif
        holder.cardMood.setOnClickListener {
            if (!isEnabled) return@setOnClickListener

            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            onItemClick?.invoke(mood)
        }
    }

    override fun getItemCount(): Int = moodList.size
}
