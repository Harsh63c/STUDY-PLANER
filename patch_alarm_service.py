import re

with open("app/src/main/java/com/example/services/AlarmService.kt", "r") as f:
    content = f.read()

# Import SettingsPreferences
if "com.example.utils.SettingsPreferences" not in content:
    content = content.replace("import com.example.ui.screens.studyplanner.AlarmRingingActivity", "import com.example.ui.screens.studyplanner.AlarmRingingActivity\nimport com.example.utils.SettingsPreferences\nimport android.app.AlarmManager")

# Add Snooze handling in onStartCommand
on_start_command = """    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_ALARM") {
            stopSelf()
            return START_NOT_STICKY
        }
        
        val title = intent?.getStringExtra("TITLE") ?: "Alarm"
        val message = intent?.getStringExtra("MESSAGE") ?: "Time to study!"
        val notifId = intent?.getIntExtra("NOTIF_ID", 1) ?: 1
        val isStudyPlan = intent?.getBooleanExtra("IS_STUDY_PLAN", false) ?: false
        
        if (intent?.action == "SNOOZE_ALARM") {
            val snoozeDuration = SettingsPreferences.getSnoozeDuration(this)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val snoozeTimeMillis = System.currentTimeMillis() + (snoozeDuration * 60 * 1000)
            val snoozeIntent = Intent(this, com.example.services.AlarmReceiver::class.java).apply {
                if (isStudyPlan) {
                    putExtra("STUDY_PLAN_ID", notifId)
                    putExtra("ALARM_TYPE", 1)
                } else {
                    putExtra("TASK_ID", notifId - 10000)
                }
            }
            val pendingIntent = PendingIntent.getBroadcast(
                this, 
                notifId * 10 + 99, 
                snoozeIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmClockInfo = AlarmManager.AlarmClockInfo(snoozeTimeMillis, pendingIntent)
            try {
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            stopSelf()
            return START_NOT_STICKY
        }
"""
content = re.sub(r'override fun onStartCommand.*?val isStudyPlan = intent\?\.getBooleanExtra\("IS_STUDY_PLAN", false\) \?: false', on_start_command, content, flags=re.DOTALL)

# Add Snooze action to notification
add_snooze_action = """
        val snoozeDuration = SettingsPreferences.getSnoozeDuration(this)
        val snoozeIntent = Intent(this, AlarmService::class.java).apply {
            action = "SNOOZE_ALARM"
            putExtra("TITLE", title)
            putExtra("MESSAGE", message)
            putExtra("NOTIF_ID", notifId)
            putExtra("IS_STUDY_PLAN", isStudyPlan)
        }
        val snoozePendingIntent = PendingIntent.getService(this, notifId + 5000, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(android.R.drawable.ic_popup_sync, "Snooze (${snoozeDuration}m)", snoozePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", stopPendingIntent)
"""
content = re.sub(r'val builder = NotificationCompat.Builder.*?\.addAction\(android\.R\.drawable\.ic_menu_close_clear_cancel, "Dismiss", stopPendingIntent\)', add_snooze_action, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/services/AlarmService.kt", "w") as f:
    f.write(content)
