package com.example.suarasamind.app.journal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suarasamind.app.data.JournalEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class JournalViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String? get() = auth.currentUser?.uid

    private val _journalEntries = MutableLiveData<List<JournalEntry>>()
    val journalEntries: LiveData<List<JournalEntry>> = _journalEntries

    private var journalListener: ListenerRegistration? = null

    init {
        listenToJournalEntries()
    }

    private fun listenToJournalEntries() {
        if (currentUserId == null) {
            _journalEntries.value = emptyList()
            return
        }
        journalListener = firestore.collection("users").document(currentUserId!!)
            .collection("journals")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("JournalViewModel", "Listen failed.", error)
                    _journalEntries.value = emptyList()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val entryList = snapshots.map { document ->
                        document.toObject(JournalEntry::class.java).apply { id = document.id }
                    }
                    _journalEntries.value = entryList
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        journalListener?.remove()
    }
}