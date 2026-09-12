import re

with open('app/src/main/java/com/example/services/AlarmReceiver.kt', 'r') as f:
    content = f.read()

# We need to replace the showNotification implementation.
# In showNotification, we start the AlarmService.

new_method = """    private fun showNotification(context: Context, notifId: Int, title: String, message: String, alarmType: Int, isStudyPlan: Boolean) {
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("TITLE", title)
            putExtra("MESSAGE", message)
            putExtra("NOTIF_ID", notifId)
            putExtra("IS_STUDY_PLAN", isStudyPlan)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}"""

content = re.sub(r'    private fun showNotification\(.*?\}\s*\}', new_method, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/services/AlarmReceiver.kt', 'w') as f:
    f.write(content)
