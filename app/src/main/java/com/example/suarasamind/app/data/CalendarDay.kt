// File: data/CalendarDay.kt
package com.example.suarasamind.app.data

import java.util.Date

data class CalendarDay(
    val dayNumber: Int,
    val date: Date?,
    val moodType: String?,
    val isSelected: Boolean = false
)