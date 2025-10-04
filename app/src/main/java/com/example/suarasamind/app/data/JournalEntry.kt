package com.example.suarasamind.app.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class JournalEntry(
    var id: String = "",
    val title: String = "",
    val content: String = "",
    val mood: String = "flat",
    @ServerTimestamp
    val timestamp: Date? = null
)