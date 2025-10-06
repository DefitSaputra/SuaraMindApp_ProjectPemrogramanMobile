// File: data/MoodEntry.kt
package com.example.suarasamind.app.data

import java.util.Date
import java.util.Calendar

data class MoodEntry(
    var id: String = "",
    val type: String = "", // "happy", "sad", "flat", "angry"
    val timestamp: Date? = Date(),
    val message: String = ""
) {
    // Helper function untuk mendapatkan tanggal tanpa waktu
    fun getDateOnly(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = timestamp ?: Date()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}