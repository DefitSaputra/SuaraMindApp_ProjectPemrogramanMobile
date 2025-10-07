package com.example.suarasamind.app.mood

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.suarasamind.app.data.CalendarDay
import com.example.suarasamind.app.data.MoodEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class MoodHistoryViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String get() = auth.currentUser?.uid ?: ""

    private val _calendarDays = MutableLiveData<List<CalendarDay>>()
    val calendarDays: LiveData<List<CalendarDay>> = _calendarDays

    private val _selectedDate = MutableLiveData<Date>()
    val selectedDate: LiveData<Date> = _selectedDate

    private val _selectedDateMoods = MutableLiveData<List<MoodEntry>>()
    val selectedDateMoods: LiveData<List<MoodEntry>> = _selectedDateMoods

    private var allMoodsInMonth = listOf<MoodEntry>()
    private var currentYear: Int = 0
    private var currentMonth: Int = 0

    init {
        _selectedDate.value = getStartOfDay(Date())
    }

    fun loadMoodHistory(year: Int, month: Int) {
        if (currentUserId.isEmpty()) {
            _calendarDays.value = emptyList()
            _selectedDateMoods.value = emptyList()
            return
        }

        currentYear = year
        currentMonth = month

        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1, 0, 0, 0)
        val startOfMonth = calendar.time

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfMonth = calendar.time

        firestore.collection("users").document(currentUserId)
            .collection("moods")
            .whereGreaterThanOrEqualTo("timestamp", startOfMonth)
            .whereLessThanOrEqualTo("timestamp", endOfMonth)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshots ->
                allMoodsInMonth = snapshots.mapNotNull { document ->
                    document.toObject(MoodEntry::class.java).apply { id = document.id }
                }
                generateCalendarDays()
                updateSelectedDateMoods()
            }
            .addOnFailureListener { e ->
                Log.w("MoodHistoryViewModel", "Error loading mood history", e)
                allMoodsInMonth = emptyList()
                generateCalendarDays()
                updateSelectedDateMoods()
            }
    }

    private fun generateCalendarDays() {
        val days = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance().apply { set(currentYear, currentMonth, 1) }

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        for (i in 0 until firstDayOfWeek) {
            days.add(CalendarDay(0, null, null))
        }

        val selectedDayCal = Calendar.getInstance().apply { time = _selectedDate.value!! }

        for (day in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val date = getStartOfDay(calendar.time)

            val moodForDay = allMoodsInMonth.firstOrNull { mood -> isSameDay(mood.timestamp, date) }
            val isSelected = isSameDay(date, selectedDayCal.time)

            days.add(CalendarDay(day, date, moodForDay?.type, isSelected))
        }
        _calendarDays.value = days
    }

    fun selectDate(date: Date) {
        val normalizedDate = getStartOfDay(date)
        if (_selectedDate.value != normalizedDate) {
            _selectedDate.value = normalizedDate
            generateCalendarDays() // Update kalender untuk highlight tanggal baru
            updateSelectedDateMoods()
        }
    }

    private fun updateSelectedDateMoods() {
        val selected = _selectedDate.value ?: return
        _selectedDateMoods.value = allMoodsInMonth.filter { mood -> isSameDay(mood.timestamp, selected) }
    }

    private fun isSameDay(date1: Date?, date2: Date?): Boolean {
        if (date1 == null || date2 == null) return false
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun getStartOfDay(date: Date): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}