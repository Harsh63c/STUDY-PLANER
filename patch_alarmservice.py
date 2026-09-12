import re

with open('app/src/main/java/com/example/services/AlarmService.kt', 'r') as f:
    content = f.read()

# Add imports for AlarmRingingActivity
if 'import com.example.ui.screens.studyplanner.AlarmRingingActivity' not in content:
    content = content.replace('import com.example.MainActivity', 'import com.example.MainActivity\nimport com.example.ui.screens.studyplanner.AlarmRingingActivity')

# Create the fullScreenIntent
full_screen_intent_setup = """        val fullScreenIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            putExtra("TITLE", title)
            putExtra("MESSAGE", message)
            putExtra("NOTIF_ID", notifId)
            putExtra("IS_STUDY_PLAN", isStudyPlan)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(this, notifId + 3000, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)"""

content = content.replace('        val builder = NotificationCompat.Builder(this, channelId)', full_screen_intent_setup)

# Add setFullScreenIntent to the builder
if 'setFullScreenIntent(fullScreenPendingIntent, true)' not in content:
    content = content.replace('.setContentIntent(pendingIntent)', 
                              '.setContentIntent(pendingIntent)\n            .setFullScreenIntent(fullScreenPendingIntent, true)')

with open('app/src/main/java/com/example/services/AlarmService.kt', 'w') as f:
    f.write(content)
