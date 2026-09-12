import re

with open("app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt", "r") as f:
    content = f.read()

# Add import
if "com.example.utils.SettingsPreferences" not in content:
    content = content.replace("import com.example.services.AlarmService", "import com.example.services.AlarmService\nimport com.example.utils.SettingsPreferences")

# Update snoozeAlarm signature
content = content.replace("private fun snoozeAlarm(title: String, message: String, notifId: Int, isStudyPlan: Boolean)", "private fun snoozeAlarm(title: String, message: String, notifId: Int, isStudyPlan: Boolean, durationMinutes: Int = 5)")

# Update snooze calculation
content = content.replace("val snoozeTimeMillis = System.currentTimeMillis() + (5 * 60 * 1000)", "val snoozeTimeMillis = System.currentTimeMillis() + (durationMinutes * 60 * 1000)")

# Update AlarmScreenContent signature
content = content.replace("fun AlarmScreenContent(\n    title: String,\n    message: String,\n    timeString: String,\n    onDismiss: () -> Unit,\n    onSnooze: () -> Unit\n)", "fun AlarmScreenContent(\n    title: String,\n    message: String,\n    timeString: String,\n    snoozeDuration: Int,\n    onDismiss: () -> Unit,\n    onSnooze: () -> Unit\n)")

# Update Button text
content = content.replace("Text(\"Snooze (5m)\",", "Text(\"Snooze (${snoozeDuration}m)\",")

# Pass it from setContent
new_set_content = """        val snoozeDuration = SettingsPreferences.getSnoozeDuration(this)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AlarmScreenContent(
                        title = title,
                        message = message,
                        timeString = currentTime,
                        snoozeDuration = snoozeDuration,
                        onDismiss = {
                            dismissAlarm()
                            finish()
                        },
                        onSnooze = {
                            snoozeAlarm(title, message, notifId, isStudyPlan, snoozeDuration)
                            finish()
                        }
                    )"""
content = re.sub(r'setContent \{\s*MyApplicationTheme \{\s*Surface\(\s*modifier = Modifier.fillMaxSize\(\),\s*color = MaterialTheme.colorScheme.background\s*\) \{\s*AlarmScreenContent\([\s\S]*?onSnooze = \{[\s\S]*?snoozeAlarm\(title, message, notifId, isStudyPlan\)[\s\S]*?finish\(\)\s*\}\s*\)\s*\}\s*\}\s*\}', new_set_content, content)

with open("app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt", "w") as f:
    f.write(content)
