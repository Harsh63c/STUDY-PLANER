import re

with open('app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, plan.alarm1TimeMillis, pendingIntent1)',
    'val info1 = AlarmManager.AlarmClockInfo(plan.alarm1TimeMillis, pendingIntent1)\n                alarmManager.setAlarmClock(info1, pendingIntent1)'
)
content = content.replace(
    'alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, plan.alarm2TimeMillis, pendingIntent2)',
    'val info2 = AlarmManager.AlarmClockInfo(plan.alarm2TimeMillis, pendingIntent2)\n                alarmManager.setAlarmClock(info2, pendingIntent2)'
)

with open('app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.reminderTimeMillis, pendingIntent)',
    'val info = AlarmManager.AlarmClockInfo(task.reminderTimeMillis, pendingIntent)\n            alarmManager.setAlarmClock(info, pendingIntent)'
)

with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'w') as f:
    f.write(content)
