package com.example.suarasamind.app.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suarasamind.app.data.NotificationItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String get() = auth.currentUser?.uid ?: ""

    private val _notifications = MutableLiveData<List<NotificationItem>>()
    val notifications: LiveData<List<NotificationItem>> = _notifications

    init {
        listenForNotifications()
    }

    private fun listenForNotifications() {
        if (currentUserId.isEmpty()) return

        firestore.collection("users").document(currentUserId)
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(30) // Batasi 30 notifikasi terbaru
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) {
                    _notifications.value = emptyList()
                    return@addSnapshotListener
                }
                _notifications.value = snapshots.map { doc ->
                    doc.toObject(NotificationItem::class.java).apply { id = doc.id }
                }
            }
    }

    fun markNotificationAsRead(notification: NotificationItem) {
        if (currentUserId.isEmpty() || notification.isRead) return

        firestore.collection("users").document(currentUserId)
            .collection("notifications").document(notification.id)
            .update("isRead", true)
    }
}