package com.example.suarasamind.app.services // Sesuaikan package Anda

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Dipanggil saat ada notifikasi masuk
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage.from}")
        remoteMessage.notification?.let {
            Log.d("FCM", "Notification Message Body: ${it.body}")
        }
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        sendTokenToFirestore(token)
    }

    private fun sendTokenToFirestore(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) return

        val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        userDocRef.update("fcmToken", token)
            .addOnSuccessListener { Log.d("FCM", "Token updated to Firestore.") }
            .addOnFailureListener { e -> Log.w("FCM", "Error updating token", e) }
    }
}