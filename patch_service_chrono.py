import re

with open('app/src/main/java/com/example/services/AlarmService.kt', 'r') as f:
    content = f.read()

# Modify startForegroundAlarm to take endTimeMillis
content = content.replace(
    'val isStudyPlan = intent?.getBooleanExtra("IS_STUDY_PLAN", false) ?: false\n        startForegroundAlarm(notifId, title, message, isStudyPlan)',
    '''val isStudyPlan = intent?.getBooleanExtra("IS_STUDY_PLAN", false) ?: false
        val endTimeMillis = intent?.getLongExtra("END_TIME_MILLIS", 0L) ?: 0L
        startForegroundAlarm(notifId, title, message, isStudyPlan, endTimeMillis)'''
)

content = content.replace(
    'private fun startForegroundAlarm(notifId: Int, title: String, message: String, isStudyPlan: Boolean) {',
    'private fun startForegroundAlarm(notifId: Int, title: String, message: String, isStudyPlan: Boolean, endTimeMillis: Long) {'
)

# Add chronometer to builder
chrono_logic = """
        if (endTimeMillis > System.currentTimeMillis()) {
            builder.setUsesChronometer(true)
            builder.setWhen(endTimeMillis)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setChronometerCountDown(true)
            }
        }
        
        startForeground(notifId, builder.build())"""

content = content.replace('startForeground(notifId, builder.build())', chrono_logic)

with open('app/src/main/java/com/example/services/AlarmService.kt', 'w') as f:
    f.write(content)
