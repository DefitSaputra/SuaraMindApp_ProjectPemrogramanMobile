package com.example.suarasamind.app.journal

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
            selectMood(binding.moodFlat, "flat")
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
        // Set click listener untuk ImageView mood
        binding.moodHappy.setOnClickListener { selectMood(it, "happy") }
        binding.moodFlat.setOnClickListener { selectMood(it, "flat") }
        binding.moodSad.setOnClickListener { selectMood(it, "sad") }
        binding.moodAngry.setOnClickListener { selectMood(it, "angry") }

        // Set click listener untuk parent card juga agar lebih mudah diklik
        binding.moodHappy.parent?.let { parent ->
            if (parent is View) {
                parent.setOnClickListener { selectMood(binding.moodHappy, "happy") }
            }
        }
        binding.moodFlat.parent?.let { parent ->
            if (parent is View) {
                parent.setOnClickListener { selectMood(binding.moodFlat, "flat") }
            }
        }
        binding.moodSad.parent?.let { parent ->
            if (parent is View) {
                parent.setOnClickListener { selectMood(binding.moodSad, "sad") }
            }
        }
        binding.moodAngry.parent?.let { parent ->
            if (parent is View) {
                parent.setOnClickListener { selectMood(binding.moodAngry, "angry") }
            }
        }
    }

    private fun selectMood(selectedView: View, mood: String) {
        // Reset semua mood ImageView ke state tidak terpilih
        resetAllMoodViews()

        // Set mood yang dipilih dengan opacity penuh
        selectedView.alpha = 1.0f

        // Set parent card juga ke opacity penuh untuk visual feedback yang lebih baik
        selectedView.parent?.let { parent ->
            if (parent is ViewGroup) {
                parent.alpha = 1.0f
            }
        }

        selectedMood = mood
    }

    private fun resetAllMoodViews() {
        // Reset opacity untuk semua ImageView mood
        binding.moodHappy.alpha = 0.5f
        binding.moodFlat.alpha = 0.5f
        binding.moodSad.alpha = 0.5f
        binding.moodAngry.alpha = 0.5f

        // Reset opacity untuk semua parent cards
        (binding.moodHappy.parent as? ViewGroup)?.alpha = 0.5f
        (binding.moodFlat.parent as? ViewGroup)?.alpha = 0.5f
        (binding.moodSad.parent as? ViewGroup)?.alpha = 0.5f
        (binding.moodAngry.parent as? ViewGroup)?.alpha = 0.5f
    }

    private fun saveJournalEntry() {
        val title = binding.etJournalTitle.text.toString().trim()
        val content = binding.etJournalContent.text.toString().trim()
        val userId = firebaseAuth.currentUser?.uid

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Judul dan isi jurnal tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId == null) {
            Toast.makeText(this, "Gagal mendapatkan data pengguna", Toast.LENGTH_SHORT).show()
            return
        }

        val entry = JournalEntry(title = title, content = content, mood = selectedMood)

        if (existingJournalId != null) {
            // Mode EDIT: Update jurnal yang sudah ada
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
            // Mode BUAT BARU: Tambah jurnal baru
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