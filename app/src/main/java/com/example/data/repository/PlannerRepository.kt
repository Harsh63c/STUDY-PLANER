package com.example.data.repository

import com.example.data.dao.ChapterDao
import com.example.data.dao.StudyPlanDao
import com.example.data.dao.StudySessionDao
import com.example.data.dao.SubjectDao
import com.example.data.dao.TaskDao
import com.example.data.models.Chapter
import com.example.data.models.StudyPlan
import com.example.data.models.StudySession
import com.example.data.models.Subject
import com.example.data.models.Task
import kotlinx.coroutines.flow.Flow

class PlannerRepository(
    private val taskDao: TaskDao,
    private val subjectDao: SubjectDao,
    private val chapterDao: ChapterDao,
    private val studySessionDao: StudySessionDao,
    private val studyPlanDao: StudyPlanDao
) {
    fun getTasksForDate(start: Long, end: Long) = taskDao.getTasksForDate(start, end)
    fun getTasksForDateRange(start: Long, end: Long) = taskDao.getTasksForDateRange(start, end)
    fun getUpcomingTasks(start: Long) = taskDao.getUpcomingTasks(start)
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun getTaskById(id: Int) = taskDao.getTaskById(id)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    fun getAllSubjects() = subjectDao.getAllSubjects()
    suspend fun insertSubject(subject: Subject) = subjectDao.insertSubject(subject)
    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    fun getChaptersForSubject(subjectId: Int) = chapterDao.getChaptersForSubject(subjectId)
    fun getChaptersForDate(start: Long, end: Long) = chapterDao.getChaptersForDate(start, end)
    suspend fun insertChapter(chapter: Chapter) = chapterDao.insertChapter(chapter)
    suspend fun updateChapter(chapter: Chapter) = chapterDao.updateChapter(chapter)
    suspend fun deleteChapter(chapter: Chapter) = chapterDao.deleteChapter(chapter)

    fun getAllSessions() = studySessionDao.getAllSessions()
    fun getStudyTimeForDate(start: Long, end: Long) = studySessionDao.getStudyTimeForDate(start, end)
    suspend fun insertSession(session: StudySession) = studySessionDao.insertSession(session)

    fun getStudyPlansForDateRange(start: Long, end: Long) = studyPlanDao.getStudyPlansForDateRange(start, end)
    suspend fun getStudyPlanById(id: Int) = studyPlanDao.getStudyPlanById(id)
    suspend fun insertStudyPlan(studyPlan: StudyPlan) = studyPlanDao.insertStudyPlan(studyPlan)
    suspend fun updateStudyPlan(studyPlan: StudyPlan) = studyPlanDao.updateStudyPlan(studyPlan)
    suspend fun deleteStudyPlan(studyPlan: StudyPlan) = studyPlanDao.deleteStudyPlan(studyPlan)
}
