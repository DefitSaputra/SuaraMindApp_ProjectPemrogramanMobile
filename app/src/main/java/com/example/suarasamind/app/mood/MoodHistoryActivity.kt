// File: mood/MoodHistoryActivity.kt
package com.example.suarasamind.app.mood

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suarasamind.app.adapters.CalendarAdapter
import com.example.suarasamind.app.adapters.MoodHistoryAdapter
import com.example.suarasamind.app.databinding.ActivityMoodHistoryBinding
import com.example.suarasamind.app.main.BaseActivity
import java.text.SimpleDateFormat
import java.util.*

class MoodHistoryActivity : BaseActivity<ActivityMoodHistoryBinding>() {

    private val moodHistoryViewModel: MoodHistoryViewModel by viewModels()
    private lateinit var moodHistoryAdapter: MoodHistoryAdapter
    private lateinit var calendarAdapter: CalendarAdapter

    private var currentCalendar = Calendar.getInstance()

    override fun inflateBinding(): ActivityMoodHistoryBinding {
        return ActivityMoodHistoryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setupAdapters()
        setupCalendarNav()
        observeViewModel()

        // Load data untuk bulan dan tahun saat ini
        loadMoodsForCurrentMonth()
    }

    private fun loadMoodsForCurrentMonth() {
        moodHistoryViewModel.loadMoodHistory(
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.MONTH)
        )
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupAdapters() {
        // Setup Calendar Adapter
        calendarAdapter = CalendarAdapter().apply {
            onDateClick = { date ->
                moodHistoryViewModel.selectDate(date)
            }
        }
        binding.rvCalendar.apply {
            layoutManager = GridLayoutManager(this@MoodHistoryActivity, 7)
            adapter = calendarAdapter
            // Cegah itemAnimator berkedip saat item dipilih
            itemAnimator = null
        }

        // Setup Mood History Adapter
        moodHistoryAdapter = MoodHistoryAdapter()
        binding.rvMoodList.apply {
            layoutManager = LinearLayoutManager(this@MoodHistoryActivity)
            adapter = moodHistoryAdapter
        }
    }

    private fun setupCalendarNav() {
        updateCalendarHeader()

        binding.btnPrevMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateCalendarHeader()
            loadMoodsForCurrentMonth()
        }

        binding.btnNextMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateCalendarHeader()
            loadMoodsForCurrentMonth()
        }
    }

    private fun updateCalendarHeader() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        binding.tvMonthYear.text = sdf.format(currentCalendar.time)
    }

    private fun observeViewModel() {
        moodHistoryViewModel.calendarDays.observe(this) { days ->
            calendarAdapter.submitList(days)
        }

        moodHistoryViewModel.selectedDateMoods.observe(this) { moods ->
            moodHistoryAdapter.submitList(moods)
            binding.tvNoData.visibility = if (moods.isEmpty()) View.VISIBLE else View.GONE
        }

        moodHistoryViewModel.selectedDate.observe(this) { date ->
            val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            binding.tvSelectedDate.text = sdf.format(date)
        }
    }
}