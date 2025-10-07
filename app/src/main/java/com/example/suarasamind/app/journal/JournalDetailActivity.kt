package com.example.suarasamind.app.journal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.suarasamind.app.R
import com.example.suarasamind.app.data.JournalEntry
import com.example.suarasamind.app.databinding.ActivityJournalDetailBinding
import com.example.suarasamind.app.main.BaseActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.text.SimpleDateFormat
import java.util.Locale

class JournalDetailActivity : BaseActivity<ActivityJournalDetailBinding>() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var journalId: String
    private lateinit var userId: String

    override fun inflateBinding(): ActivityJournalDetailBinding {
        return ActivityJournalDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        journalId = intent.getStringExtra("JOURNAL_ID") ?: ""
        userId = firebaseAuth.currentUser?.uid ?: ""

        if (journalId.isEmpty() || userId.isEmpty()) {
            Toast.makeText(this, "Jurnal tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        loadJournalEntry()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadJournalEntry() {
        firestore.collection("users").document(userId)
            .collection("journals").document(journalId)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (document != null && document.exists()) {
                    val entry = document.toObject<JournalEntry>()
                    entry?.let { displayJournal(it) }
                } else {
                    Toast.makeText(this, "Jurnal ini mungkin telah dihapus.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
    }

    private fun displayJournal(entry: JournalEntry) {
        binding.tvJournalTitle.text = entry.title
        binding.tvJournalContent.text = entry.content

        entry.timestamp?.let {
            val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            binding.tvJournalDate.text = sdf.format(it)
        }

        val moodIcon = when (entry.mood) {
            "happy" -> R.drawable.emo_happy
            "sad" -> R.drawable.emo_sad
            "angry" -> R.drawable.emo_angry
            else -> R.drawable.emo_flat
        }
        binding.ivMoodIndicator.setImageResource(moodIcon)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_post_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val intent = Intent(this, WriteJournalActivity::class.java)
                intent.putExtra("JOURNAL_ID", journalId)
                startActivity(intent)
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Jurnal")
            .setMessage("Apakah Anda yakin ingin menghapus jurnal ini secara permanen?")
            .setPositiveButton("Ya, Hapus") { _, _ ->
                deleteJournalEntry()
            }
            .setNegativeButton("Batal", null)
            .setIcon(R.drawable.ic_delete)
            .show()
    }

    private fun deleteJournalEntry() {
        firestore.collection("users").document(userId)
            .collection("journals").document(journalId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Jurnal berhasil dihapus", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menghapus jurnal: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}