with open('app/src/main/java/com/example/services/AlarmService.kt', 'r') as f:
    content = f.read()

# Add deletePendingIntent definition
if 'val deletePendingIntent' not in content:
    content = content.replace('val fullScreenIntent', 
                              'val deletePendingIntent = PendingIntent.getService(this, notifId + 4000, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)\n        val fullScreenIntent')

with open('app/src/main/java/com/example/services/AlarmService.kt', 'w') as f:
    f.write(content)
