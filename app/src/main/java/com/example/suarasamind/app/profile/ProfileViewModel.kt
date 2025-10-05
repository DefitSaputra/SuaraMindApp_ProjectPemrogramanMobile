package com.example.suarasamind.app.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suarasamind.app.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage

class ProfileViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance() // PENAMBAHAN: Firebase Storage
    private val currentUserId: String get() = auth.currentUser?.uid ?: ""

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _journalCount = MutableLiveData<Int>()
    val journalCount: LiveData<Int> = _journalCount

    private val _moodCount = MutableLiveData<Int>()
    val moodCount: LiveData<Int> = _moodCount

    // PENAMBAHAN: LiveData untuk memberitahu Fragment status upload
    private val _uploadStatus = MutableLiveData<String>()
    val uploadStatus: LiveData<String> = _uploadStatus

    private var userListener: ListenerRegistration? = null
    private var journalListener: ListenerRegistration? = null
    private var moodListener: ListenerRegistration? = null

    init {
        loadAllData()
    }

    private fun loadAllData() {
        if (currentUserId.isEmpty()) return

        userListener = firestore.collection("users").document(currentUserId)
            .addSnapshotListener { document, _ ->
                document?.toObject<User>()?.let { _user.value = it }
            }

        journalListener = firestore.collection("users").document(currentUserId).collection("journals")
            .addSnapshotListener { snapshot, _ -> _journalCount.value = snapshot?.size() ?: 0 }

        moodListener = firestore.collection("users").document(currentUserId).collection("moods")
            .addSnapshotListener { snapshot, _ -> _moodCount.value = snapshot?.size() ?: 0 }
    }

    fun updateUserName(newName: String) {
        if (currentUserId.isEmpty() || newName.isBlank()) return
        firestore.collection("users").document(currentUserId)
            .update("fullName", newName)
            .addOnFailureListener { e -> Log.w("ProfileViewModel", "Error updating name", e) }
    }

    fun uploadProfileImage(imageUri: Uri) {
        if (currentUserId.isEmpty()) return
        _uploadStatus.value = "Mengupload foto..."
        val storageRef = storage.reference.child("profile_images/$currentUserId")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    updateProfileImageUrl(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = "Upload gagal: ${e.message}"
                Log.w("ProfileViewModel", "Error uploading image", e)
            }
    }

    private fun updateProfileImageUrl(imageUrl: String) {
        firestore.collection("users").document(currentUserId)
            .update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                _uploadStatus.value = "Foto profil berhasil diperbarui!"
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = "Gagal menyimpan URL: ${e.message}"
                Log.w("ProfileViewModel", "Error updating URL", e)
            }
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