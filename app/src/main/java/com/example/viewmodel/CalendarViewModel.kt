package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.models.Task
import com.example.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class CalendarViewModel(private val repository: PlannerRepository) : ViewModel() {

    private val _selectedDateMillis = MutableStateFlow(getStartOfDay(System.currentTimeMillis()))
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis

    private val _currentMonthMillis = MutableStateFlow(getStartOfMonth(System.currentTimeMillis()))
    val currentMonthMillis: StateFlow<Long> = _currentMonthMillis

    val taskDatesForMonth: StateFlow<Set<Long>> = _currentMonthMillis.flatMapLatest { monthStart ->
        val monthEnd = getEndOfMonth(monthStart)
        repository.getTasksForDateRange(monthStart, monthEnd)
    }.flatMapLatest { tasks ->
        kotlinx.coroutines.flow.flowOf(tasks.map { getStartOfDay(it.dateMillis) }.toSet())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val tasksForMonth: StateFlow<List<Task>> = _currentMonthMillis.flatMapLatest { monthStart ->
        val monthEnd = getEndOfMonth(monthStart)
        repository.getTasksForDateRange(monthStart, monthEnd)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasksForSelectedDate: StateFlow<List<Task>> = _selectedDateMillis.flatMapLatest { date ->
        val endOfDay = date + 24 * 60 * 60 * 1000 - 1
        repository.getTasksForDate(date, endOfDay)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectDate(dateMillis: Long) {
        _selectedDateMillis.value = getStartOfDay(dateMillis)
        // Also update current month if the selected date is in a different month
        _currentMonthMillis.value = getStartOfMonth(dateMillis)
    }

    fun changeMonth(offset: Int) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = _currentMonthMillis.value
            add(Calendar.MONTH, offset)
        }
        _currentMonthMillis.value = getStartOfMonth(cal.timeInMillis)
    }

    fun addTask(title: String, startTime: Long?, endTime: Long?, reminderTime: Long?, dateMillis: Long, onInserted: (Task) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insertTask(
                Task(
                    title = title,
                    dateMillis = getStartOfDay(dateMillis),
                    startTimeMillis = startTime,
                    endTimeMillis = endTime,
                    reminderTimeMillis = reminderTime
                )
            )
            onInserted(Task(id = id.toInt(), title = title, dateMillis = getStartOfDay(dateMillis), startTimeMillis = startTime, endTimeMillis = endTime, reminderTimeMillis = reminderTime))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(dateMillis = getStartOfDay(task.dateMillis)))
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    private fun getStartOfDay(timeInMillis: Long): Long {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getStartOfMonth(timeInMillis: Long): Long {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getEndOfMonth(timeInMillis: Long): Long {
        val cal = Calendar.getInstance().apply {
            this.timeInMillis = getStartOfMonth(timeInMillis)
            add(Calendar.MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }
        return cal.timeInMillis
    }
}

class CalendarViewModelFactory(private val repository: PlannerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
