package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "tasks")
@Serializable
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dateMillis: Long, // the day it is scheduled for
    val startTimeMillis: Long?, // optional start time
    val endTimeMillis: Long?, // optional end time
        val reminderTimeMillis: Long?, // optional reminder time
    val isCompleted: Boolean = false,
    val repeatMode: String = "None", // None, Daily, Weekdays, Weekends, Custom
    val customRepeatDays: String = "", // Comma-separated like "1,2,3"
    val skipNextOccurrence: Boolean = false
)

@Entity(tableName = "subjects")
@Serializable
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "chapters")
@Serializable
data class Chapter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val title: String,
    val targetDateMillis: Long?,
    val targetDurationMinutes: Int?, // optional target duration in minutes
    val isCompleted: Boolean = false
)

@Entity(tableName = "study_plans")
@Serializable
data class StudyPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val chapter: String,
    val dateMillis: Long,
    val startTimeMillis: Long,
    val targetDurationMinutes: Int,
    val notes: String,
    val alarm1TimeMillis: Long?,
    val alarm2TimeMillis: Long?,
    val isCompleted: Boolean = false,
        val inProgress: Boolean = false,
    val status: String = "Scheduled", // "Scheduled", "Alarm 1 Triggered", "Waiting for Backup Alarm", "In Progress", "Completed", "Dismissed"
    val repeatMode: String = "None",
    val customRepeatDays: String = "",
    val skipNextOccurrence: Boolean = false
)
@Entity(tableName = "study_sessions")
@Serializable
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chapterId: Int?, // can be null if a generic session
    val startTimeMillis: Long,
    val durationMinutes: Int
)
