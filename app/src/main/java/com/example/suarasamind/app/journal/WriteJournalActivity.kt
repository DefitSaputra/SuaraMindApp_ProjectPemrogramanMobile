package com.example.suarasamind.app.journal

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.suarasamind.app.data.JournalEntry
import com.example.suarasamind.app.databinding.ActivityWriteJournalBinding
import com.example.suarasamind.app.main.BaseActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class WriteJournalActivity : BaseActivity<ActivityWriteJournalBinding>() {

    private lateinit var firestore: FirebaseFirestore
    private var selectedMood: String = "flat"

    private var existingJournalId: String? = null

    override fun inflateBinding(): ActivityWriteJournalBinding {
        return ActivityWriteJournalBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        existingJournalId = intent.getStringExtra("JOURNAL_ID")

        if (existingJournalId != null) {
            binding.toolbar.title = "Edit Jurnal"
            loadExistingJournal()
        } else {
            binding.toolbar.title = "Tulis Jurnal"
            selectMood(binding.moodFlat, "flat") // Atur mood default
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
        setupMoodSelectors()
        binding.btnSaveJournal.setOnClickListener { saveJournalEntry() }
    }

    private fun loadExistingJournal() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null || existingJournalId == null) return

        firestore.collection("users").document(userId)
            .collection("journals").document(existingJournalId!!)
            .get()
            .addOnSuccessListener { document ->
                val entry = document.toObject<JournalEntry>()
                if (entry != null) {
                    binding.etJournalTitle.setText(entry.title)
                    binding.etJournalContent.setText(entry.content)

                    val moodView = when(entry.mood) {
                        "happy" -> binding.moodHappy
                        "sad" -> binding.moodSad
                        "angry" -> binding.moodAngry
                        else -> binding.moodFlat
                    }
                    selectMood(moodView, entry.mood)
                }
            }
    }

    private fun setupMoodSelectors() {
        binding.moodHappy.setOnClickListener { selectMood(it, "happy") }
        binding.moodFlat.setOnClickListener { selectMood(it, "flat") }
        binding.moodSad.setOnClickListener { selectMood(it, "sad") }
        binding.moodAngry.setOnClickListener { selectMood(it, "angry") }
    }

    private fun selectMood(selectedView: View, mood: String) {
        binding.moodHappy.alpha = 0.5f
        binding.moodFlat.alpha = 0.5f
        binding.moodSad.alpha = 0.5f
        binding.moodAngry.alpha = 0.5f

        selectedView.alpha = 1.0f
        selectedMood = mood
    }

    private fun saveJournalEntry() {
        val title = binding.etJournalTitle.text.toString().trim()
        val content = binding.etJournalContent.text.toString().trim()
        val userId = firebaseAuth.currentUser?.uid

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Judul dan isi jurnal tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (userId == null) { /* ... */ return }

        val entry = JournalEntry(title = title, content = content, mood = selectedMood)

        // PERUBAHAN 3: Logika penyimpanan dibedakan antara edit dan buat baru
        if (existingJournalId != null) {
            firestore.collection("users").document(userId)
                .collection("journals").document(existingJournalId!!)
                .set(entry)
                .addOnSuccessListener {
                    Toast.makeText(this, "Jurnal berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal memperbarui: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            firestore.collection("users").document(userId)
                .collection("journals").add(entry)
                .addOnSuccessListener {
                    Toast.makeText(this, "Jurnal berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}