package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.Task
import com.example.data.models.Subject
import com.example.data.models.Chapter
import com.example.data.models.StudySession
import com.example.data.models.StudyPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay ORDER BY startTimeMillis ASC")
    fun getTasksForDate(startOfDay: Long, endOfDay: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dateMillis >= :startOfRange AND dateMillis <= :endOfRange")
    fun getTasksForDateRange(startOfRange: Long, endOfRange: Long): Flow<List<Task>>
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasksSync(): List<Task>

    @Query("SELECT * FROM tasks WHERE dateMillis >= :startOfDay ORDER BY dateMillis ASC, startTimeMillis ASC")
    fun getUpcomingTasks(startOfDay: Long): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long
    
    @Delete
    suspend fun deleteSubject(subject: Subject)
}

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId")
    fun getChaptersForSubject(subjectId: Int): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE targetDateMillis >= :startOfDay AND targetDateMillis <= :endOfDay")
    fun getChaptersForDate(startOfDay: Long, endOfDay: Long): Flow<List<Chapter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter): Long

    @Update
    suspend fun updateChapter(chapter: Chapter)

    @Delete
    suspend fun deleteChapter(chapter: Chapter)
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY startTimeMillis DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE startTimeMillis >= :startOfDay AND startTimeMillis <= :endOfDay")
    fun getStudyTimeForDate(startOfDay: Long, endOfDay: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long
}

@Dao
interface StudyPlanDao {
    @Query("SELECT * FROM study_plans WHERE dateMillis >= :startOfRange AND dateMillis <= :endOfRange ORDER BY dateMillis ASC, startTimeMillis ASC")
    fun getStudyPlansForDateRange(startOfRange: Long, endOfRange: Long): Flow<List<StudyPlan>>
    
    @Query("SELECT * FROM study_plans WHERE id = :id")
    suspend fun getStudyPlanById(id: Int): StudyPlan?

    @Query("SELECT * FROM study_plans")
    suspend fun getAllStudyPlansSync(): List<StudyPlan>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyPlan(studyPlan: StudyPlan): Long

    @Update
    suspend fun updateStudyPlan(studyPlan: StudyPlan)

    @Delete
    suspend fun deleteStudyPlan(studyPlan: StudyPlan)
}
