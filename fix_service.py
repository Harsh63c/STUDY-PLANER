with open('app/src/main/java/com/example/services/AlarmService.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'startForegroundAlarm(notifId, title, message, isStudyPlan)',
    'val endTimeMillis = intent?.getLongExtra("END_TIME_MILLIS", 0L) ?: 0L\n        startForegroundAlarm(notifId, title, message, isStudyPlan, endTimeMillis)'
)

with open('app/src/main/java/com/example/services/AlarmService.kt', 'w') as f:
    f.write(content)
