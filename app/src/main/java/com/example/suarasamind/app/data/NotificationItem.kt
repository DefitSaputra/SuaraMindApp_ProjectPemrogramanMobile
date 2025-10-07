package com.example.suarasamind.app.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class NotificationItem(
    var id: String = "",
    val message: String = "",
    val type: String = "",
    val relatedId: String = "",
    val isRead: Boolean = false,
    @ServerTimestamp
    val timestamp: Date? = null
)