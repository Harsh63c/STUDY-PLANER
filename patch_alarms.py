import re

with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.reminderTimeMillis, pendingIntent)',
    '''try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.reminderTimeMillis, pendingIntent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }'''
)

with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'w') as f:
    f.write(content)


with open('app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, plan.alarm1TimeMillis, pendingIntent1)',
    '''try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, plan.alarm1TimeMillis, pendingIntent1)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }'''
)

content = content.replace(
    'alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, plan.alarm2TimeMillis, pendingIntent2)',
    '''try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, plan.alarm2TimeMillis, pendingIntent2)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }'''
)

with open('app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt', 'w') as f:
    f.write(content)
