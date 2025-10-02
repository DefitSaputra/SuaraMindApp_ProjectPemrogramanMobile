package com.example.suarasamind.app.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.MoodData

class MoodAdapter(private val moodList: List<MoodData>) :
    RecyclerView.Adapter<MoodAdapter.ViewHolder>() {

    // Listener untuk menangani klik pada mood
    var onItemClick: ((MoodData) -> Unit)? = null
    private var selectedPosition = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moodImage: ImageView = view.findViewById(R.id.iv_mood)
        val moodLabel: TextView = view.findViewById(R.id.tv_mood_label)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Update selected position
                    val previousPosition = selectedPosition
                    selectedPosition = position

                    // Notify adapter to update UI
                    if (previousPosition != -1) {
                        notifyItemChanged(previousPosition)
                    }
                    notifyItemChanged(position)

                    // Trigger callback
                    onItemClick?.invoke(moodList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_tracker, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mood = moodList[position]
        holder.moodImage.setImageResource(mood.drawableResId)

        // Set label berdasarkan type
        holder.moodLabel.text = when (mood.type) {
            "sad" -> "Sedih"
            "angry" -> "Marah"
            "flat" -> "Datar"
            "happy" -> "Senang"
            else -> ""
        }

        // Apply selection effect
        if (position == selectedPosition) {
            // Selected state
            holder.moodImage.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).start()

            // Create background drawable
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(ContextCompat.getColor(holder.itemView.context, mood.backgroundColorResId))
                setStroke(4, ContextCompat.getColor(holder.itemView.context, mood.borderColorResId))
            }
            holder.moodImage.background = drawable
            holder.moodImage.alpha = 1.0f
        } else {
            // Unselected state
            holder.moodImage.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            holder.moodImage.background = null
            holder.moodImage.alpha = 0.7f
        }
    }

    override fun getItemCount() = moodList.size
}