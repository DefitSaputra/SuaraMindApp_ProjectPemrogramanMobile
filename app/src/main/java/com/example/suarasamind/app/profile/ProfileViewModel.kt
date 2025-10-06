package com.example.suarasamind.app.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suarasamind.app.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject

class ProfileViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String get() = auth.currentUser?.uid ?: ""

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _journalCount = MutableLiveData<Int>()
    val journalCount: LiveData<Int> = _journalCount

    private val _moodCount = MutableLiveData<Int>()
    val moodCount: LiveData<Int> = _moodCount

    // --- DIHAPUS ---
    // private val _uploadStatus = MutableLiveData<String>()
    // val uploadStatus: LiveData<String> = _uploadStatus

    private var userListener: ListenerRegistration? = null
    private var journalListener: ListenerRegistration? = null
    private var moodListener: ListenerRegistration? = null

    init {
        attachListeners()
    }

    private fun attachListeners() {
        if (currentUserId.isEmpty()) return

        userListener = firestore.collection("users").document(currentUserId)
            .addSnapshotListener { document, error ->
                if (error != null) { Log.w("ProfileVM", "User listener error", error); return@addSnapshotListener }
                document?.toObject<User>()?.let { _user.value = it }
            }

        journalListener = firestore.collection("users").document(currentUserId).collection("journals")
            .addSnapshotListener { snapshot, error ->
                _journalCount.value = snapshot?.size() ?: 0
            }

        moodListener = firestore.collection("users").document(currentUserId).collection("moods")
            .addSnapshotListener { snapshot, error ->
                _moodCount.value = snapshot?.size() ?: 0
            }
    }

    // --- FUNGSI BARU ---
    fun updateAvatar(avatarId: String) {
        if (currentUserId.isEmpty()) return
        firestore.collection("users").document(currentUserId)
            .update("avatarId", avatarId)
            .addOnSuccessListener {
                Log.d("ProfileViewModel", "Avatar updated successfully to $avatarId")
            }
            .addOnFailureListener { e ->
                Log.w("ProfileViewModel", "Error updating avatar", e)
            }
    }

    // --- FUNGSI TERKAIT STORAGE DIHAPUS ---
    // fun uploadProfileImage(imageUri: Uri) { ... }
    // private fun updateProfileImageUrl(imageUrl: String) { ... }

    fun updateUserName(newName: String) {
        if (currentUserId.isEmpty() || newName.isBlank()) return
        firestore.collection("users").document(currentUserId)
            .update("fullName", newName)
            .addOnFailureListener { e -> Log.w("ProfileViewModel", "Error updating name", e) }
    }

    fun logout() {
        auth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
        journalListener?.remove()
        moodListener?.remove()
    }
}