import re

with open('app/src/main/java/com/example/services/AlarmService.kt', 'r') as f:
    content = f.read()

old_code = """        startForeground(notifId, builder.build())
        
        if (endTimeMillis > System.currentTimeMillis() && isStudyPlan) {"""

new_code = """        if (endTimeMillis > System.currentTimeMillis() && !isStudyPlan) {
            builder.setUsesChronometer(true)
            builder.setWhen(endTimeMillis)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setChronometerCountDown(true)
            }
        }
        
        startForeground(notifId, builder.build())
        
        if (endTimeMillis > System.currentTimeMillis() && isStudyPlan) {"""

content = content.replace(old_code, new_code)

with open('app/src/main/java/com/example/services/AlarmService.kt', 'w') as f:
    f.write(content)
