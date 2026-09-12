import re

with open('app/src/main/java/com/example/services/AlarmService.kt', 'r') as f:
    content = f.read()

delete_intent_setup = """        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = "STOP_ALARM"
        }
        val stopPendingIntent = PendingIntent.getService(this, notifId + 1000, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        
        val deletePendingIntent = PendingIntent.getService(this, notifId + 4000, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)"""

content = content.replace("""        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = "STOP_ALARM"
        }
        val stopPendingIntent = PendingIntent.getService(this, notifId + 1000, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)""", delete_intent_setup)

if '.setDeleteIntent(deletePendingIntent)' not in content:
    content = content.replace('.setContentIntent(pendingIntent)', 
                              '.setContentIntent(pendingIntent)\n            .setDeleteIntent(deletePendingIntent)')

with open('app/src/main/java/com/example/services/AlarmService.kt', 'w') as f:
    f.write(content)
