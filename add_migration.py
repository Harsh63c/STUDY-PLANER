import re

with open("app/src/main/java/com/example/data/AppDatabase.kt", "r") as f:
    content = f.read()

migration_code = """import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

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
"""

if "MIGRATION_2_3" not in content:
    content = content.replace("import androidx.room.RoomDatabase", "import androidx.room.RoomDatabase\nimport androidx.sqlite.db.SupportSQLiteDatabase\nimport androidx.room.migration.Migration")
    content = content.replace("companion object {", "companion object {\n        val MIGRATION_2_3 = object : Migration(2, 3) {\n            override fun migrate(database: SupportSQLiteDatabase) {\n                database.execSQL(\"ALTER TABLE tasks ADD COLUMN repeatMode TEXT NOT NULL DEFAULT 'None'\")\n                database.execSQL(\"ALTER TABLE tasks ADD COLUMN customRepeatDays TEXT NOT NULL DEFAULT ''\")\n                database.execSQL(\"ALTER TABLE tasks ADD COLUMN skipNextOccurrence INTEGER NOT NULL DEFAULT 0\")\n                database.execSQL(\"ALTER TABLE study_plans ADD COLUMN repeatMode TEXT NOT NULL DEFAULT 'None'\")\n                database.execSQL(\"ALTER TABLE study_plans ADD COLUMN customRepeatDays TEXT NOT NULL DEFAULT ''\")\n                database.execSQL(\"ALTER TABLE study_plans ADD COLUMN skipNextOccurrence INTEGER NOT NULL DEFAULT 0\")\n            }\n        }\n")
    content = content.replace(".fallbackToDestructiveMigration()", ".addMigrations(MIGRATION_2_3).fallbackToDestructiveMigration()")

with open("app/src/main/java/com/example/data/AppDatabase.kt", "w") as f:
    f.write(content)
