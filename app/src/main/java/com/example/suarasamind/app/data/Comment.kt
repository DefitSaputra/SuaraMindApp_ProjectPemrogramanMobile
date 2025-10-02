package com.example.suarasamind.app.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Comment(
    var id: String = "",
    val authorId: String = "",
    val authorUsername: String = "",
    val content: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
)
