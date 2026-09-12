import re

with open('app/src/main/java/com/example/services/AlarmReceiver.kt', 'r') as f:
    content = f.read()

new_imports = """import android.media.AudioAttributes
import android.media.RingtoneManager
"""
content = content.replace('import android.os.Build', new_imports + 'import android.os.Build')

old_channel_setup = """        val channelId = "study_planner_alarms"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Study Planner Alarms", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }"""

new_channel_setup = """        val channelId = "study_planner_alarms_v2"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Study Planner Alarms", NotificationManager.IMPORTANCE_HIGH)
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            channel.setSound(alarmSound, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }"""

content = content.replace(old_channel_setup, new_channel_setup)

with open('app/src/main/java/com/example/services/AlarmReceiver.kt', 'w') as f:
    f.write(content)
