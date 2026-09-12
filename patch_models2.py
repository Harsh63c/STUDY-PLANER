import re

with open("app/src/main/java/com/example/data/models/Models.kt", "r") as f:
    content = f.read()

study_plan_replacement = """    val inProgress: Boolean = false,
    val status: String = "Scheduled", // "Scheduled", "Alarm 1 Triggered", "Waiting for Backup Alarm", "In Progress", "Completed", "Dismissed"
    val repeatMode: String = "None",
    val customRepeatDays: String = "",
    val skipNextOccurrence: Boolean = false
)"""
content = re.sub(r'val inProgress: Boolean = false,.*?val status: String = "Scheduled".*?\)', study_plan_replacement, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/data/models/Models.kt", "w") as f:
    f.write(content)
