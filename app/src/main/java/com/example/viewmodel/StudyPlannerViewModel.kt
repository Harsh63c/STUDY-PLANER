package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.models.Chapter
import com.example.data.models.StudyPlan
import com.example.data.models.Subject
import com.example.data.repository.PlannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class StudyPlannerViewModel(private val repository: PlannerRepository) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = repository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDateMillis = MutableStateFlow(getStartOfDay(System.currentTimeMillis()))
    val selectedDateMillis: StateFlow<Long> = _selectedDateMillis

    private val _currentMonthMillis = MutableStateFlow(getStartOfMonth(System.currentTimeMillis()))
    val currentMonthMillis: StateFlow<Long> = _currentMonthMillis

    val studyPlanDatesForMonth: StateFlow<Set<Long>> = _currentMonthMillis.flatMapLatest { monthStart ->
        val monthEnd = getEndOfMonth(monthStart)
        repository.getStudyPlansForDateRange(monthStart, monthEnd)
    }.flatMapLatest { plans ->
        kotlinx.coroutines.flow.flowOf(plans.map { getStartOfDay(it.dateMillis) }.toSet())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val studyPlansForMonth: StateFlow<List<StudyPlan>> = _currentMonthMillis.flatMapLatest { monthStart ->
        val monthEnd = getEndOfMonth(monthStart)
        repository.getStudyPlansForDateRange(monthStart, monthEnd)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectDate(dateMillis: Long) {
        _selectedDateMillis.value = getStartOfDay(dateMillis)
        _currentMonthMillis.value = getStartOfMonth(dateMillis)
    }

    fun changeMonth(offset: Int) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = _currentMonthMillis.value
            add(Calendar.MONTH, offset)
        }
        _currentMonthMillis.value = getStartOfMonth(cal.timeInMillis)
    }

    fun addStudyPlan(studyPlan: StudyPlan, onInserted: (StudyPlan) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insertStudyPlan(studyPlan)
            onInserted(studyPlan.copy(id = id.toInt()))
        }
    }

    fun updateStudyPlan(studyPlan: StudyPlan) {
        viewModelScope.launch {
            repository.updateStudyPlan(studyPlan)
        }
    }

    fun deleteStudyPlan(studyPlan: StudyPlan) {
        viewModelScope.launch {
            repository.deleteStudyPlan(studyPlan)
        }
    }

    fun addSubject(name: String) {
        viewModelScope.launch {
            repository.insertSubject(Subject(name = name))
        }
    }
    
    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    fun getChaptersForSubject(subjectId: Int) = repository.getChaptersForSubject(subjectId)

    fun addChapter(subjectId: Int, title: String) {
        viewModelScope.launch {
            repository.insertChapter(Chapter(subjectId = subjectId, title = title, targetDateMillis = null, targetDurationMinutes = null))
        }
    }

    fun deleteChapter(chapter: Chapter) {
        viewModelScope.launch {
            repository.deleteChapter(chapter)
        }
    }
    
    fun saveStudySession(subjectId: Int, chapterId: Int, durationMinutes: Int, studyPlanId: Int?) {
        viewModelScope.launch {
            repository.insertSession(
                com.example.data.models.StudySession(
                    chapterId = chapterId,
                    startTimeMillis = System.currentTimeMillis() - (durationMinutes * 60 * 1000L),
                    durationMinutes = durationMinutes
                )
            )
            if (studyPlanId != null) {
                val plan = repository.getStudyPlanById(studyPlanId)
                if (plan != null) {
                    repository.updateStudyPlan(plan.copy(isCompleted = true, inProgress = false, status = "Completed"))
                }
            }
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

class StudyPlannerViewModelFactory(private val repository: PlannerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyPlannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudyPlannerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
