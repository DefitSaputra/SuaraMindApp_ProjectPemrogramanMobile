package com.example.suarasamind.app.journal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.adapters.JournalAdapter
import com.example.suarasamind.app.databinding.FragmentJournalBinding

class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi ViewModel
    private val journalViewModel: JournalViewModel by viewModels()

    private lateinit var journalAdapter: JournalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.fabAddJournal.setOnClickListener {
            startActivity(Intent(requireActivity(), WriteJournalActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        // Inisialisasi adapter dengan list kosong, data akan diisi oleh ViewModel
        journalAdapter = JournalAdapter(emptyList())
        binding.rvJournalEntries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = journalAdapter
        }
    }

    private fun observeViewModel() {
        // Mengamati perubahan data dari ViewModel
        journalViewModel.journalEntries.observe(viewLifecycleOwner) { entryList ->
            // Saat data baru datang, update adapter
            journalAdapter = JournalAdapter(entryList)
            binding.rvJournalEntries.adapter = journalAdapter

            // Atur listener klik untuk item di dalam adapter yang baru
            // PERUBAHAN: Ganti Toast dengan Intent untuk membuka halaman detail
            journalAdapter.onItemClick = { journalEntry ->
                val intent = Intent(requireActivity(), JournalDetailActivity::class.java)
                intent.putExtra("JOURNAL_ID", journalEntry.id)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}