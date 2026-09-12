import re

with open("app/src/main/java/com/example/data/models/Models.kt", "r") as f:
    content = f.read()

# For Task
task_replacement = """    val reminderTimeMillis: Long?, // optional reminder time
    val isCompleted: Boolean = false,
    val repeatMode: String = "None", // None, Daily, Weekdays, Weekends, Custom
    val customRepeatDays: String = "", // Comma-separated like "1,2,3"
    val skipNextOccurrence: Boolean = false
)"""
content = re.sub(r'val reminderTimeMillis: Long\?, // optional reminder time\s+val isCompleted: Boolean = false\s*\)', task_replacement, content)

# For StudyPlan
study_plan_replacement = """    val inProgress: Boolean = false,
    val status: String = "Scheduled", // "Scheduled", "Alarm 1 Triggered", "Waiting for Backup Alarm", "In Progress", "Completed", "Dismissed"
    val repeatMode: String = "None",
    val customRepeatDays: String = "",
    val skipNextOccurrence: Boolean = false
)"""
content = re.sub(r'val inProgress: Boolean = false,\s+val status: String = "Scheduled".*?\)', study_plan_replacement, content)

with open("app/src/main/java/com/example/data/models/Models.kt", "w") as f:
    f.write(content)
