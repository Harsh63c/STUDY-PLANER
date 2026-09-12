package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import com.example.data.models.Chapter
import com.example.data.models.StudySession
import com.example.data.models.StudyPlan
import com.example.data.models.Subject
import com.example.data.models.Task
import com.example.data.dao.ChapterDao
import com.example.data.dao.StudyPlanDao
import com.example.data.dao.StudySessionDao
import com.example.data.dao.SubjectDao
import com.example.data.dao.TaskDao

@Database(entities = [Task::class, Subject::class, Chapter::class, StudySession::class, StudyPlan::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subjectDao(): SubjectDao
    abstract fun chapterDao(): ChapterDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun studyPlanDao(): StudyPlanDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN repeatMode TEXT NOT NULL DEFAULT 'None'")
                database.execSQL("ALTER TABLE tasks ADD COLUMN customRepeatDays TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE tasks ADD COLUMN skipNextOccurrence INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE study_plans ADD COLUMN repeatMode TEXT NOT NULL DEFAULT 'None'")
                database.execSQL("ALTER TABLE study_plans ADD COLUMN customRepeatDays TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE study_plans ADD COLUMN skipNextOccurrence INTEGER NOT NULL DEFAULT 0")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study_planner_database"
                ).addMigrations(MIGRATION_2_3).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
