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
        // PERUBAHAN 1: Inisialisasi adapter baru tanpa mengirim list
        journalAdapter = JournalAdapter()
        binding.rvJournalEntries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = journalAdapter
        }

        // PERUBAHAN 2: Set click listener SEKALI SAJA di sini, bukan di dalam observer
        journalAdapter.onItemClick = { journalEntry ->
            val intent = Intent(requireActivity(), JournalDetailActivity::class.java)
            intent.putExtra("JOURNAL_ID", journalEntry.id)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        journalViewModel.journalEntries.observe(viewLifecycleOwner) { entryList ->
            // PERUBAHAN 3: Gunakan submitList untuk mengirim data baru ke adapter.
            // Ini jauh lebih efisien dan akan menangani animasi secara otomatis.
            journalAdapter.submitList(entryList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}