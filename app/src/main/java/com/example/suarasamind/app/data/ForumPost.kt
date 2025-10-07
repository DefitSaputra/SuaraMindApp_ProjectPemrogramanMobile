package com.example.suarasamind.app.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ForumPost(
    var id: String = "",
    val authorId: String = "",
    val authorUsername: String = "Anonim",
    val title: String = "",
    val content: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    var commentCount: Long = 0,
    var supportCount: Long = 0,
    val supporters: List<String> = emptyList()
)

