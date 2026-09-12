import re

with open('app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt', 'r') as f:
    content = f.read()

# Replace the alarm1 scheduling logic
old_alarm1 = '''        if (plan.alarm1TimeMillis != null && plan.alarm1TimeMillis > System.currentTimeMillis() && !plan.inProgress && !plan.isCompleted) {
            try {
                val info1 = AlarmManager.AlarmClockInfo(plan.alarm1TimeMillis, pendingIntent1)
                alarmManager.setAlarmClock(info1, pendingIntent1)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }'''

new_alarm1 = '''        val actualAlarm1Time = plan.alarm1TimeMillis ?: plan.startTimeMillis
        if (actualAlarm1Time > System.currentTimeMillis() && !plan.inProgress && !plan.isCompleted) {
            try {
                val info1 = AlarmManager.AlarmClockInfo(actualAlarm1Time, pendingIntent1)
                alarmManager.setAlarmClock(info1, pendingIntent1)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }'''

content = content.replace(old_alarm1, new_alarm1)

with open('app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/services/BootReceiver.kt', 'r') as f:
    boot_content = f.read()

old_boot_alarm1 = '''                            if (plan.alarm1TimeMillis != null && plan.alarm1TimeMillis > System.currentTimeMillis()) {
                                val intent1 = Intent(context, AlarmReceiver::class.java).apply {
                                    putExtra("STUDY_PLAN_ID", plan.id)
                                    putExtra("ALARM_TYPE", 1)
                                }
                                val pendingIntent1 = PendingIntent.getBroadcast(context, plan.id * 10 + 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                                try {
                                    val info1 = AlarmManager.AlarmClockInfo(plan.alarm1TimeMillis, pendingIntent1)
                                    alarmManager.setAlarmClock(info1, pendingIntent1)
                                } catch (e: SecurityException) {
                                    e.printStackTrace()
                                }
                            }'''

new_boot_alarm1 = '''                            val actualAlarm1Time = plan.alarm1TimeMillis ?: plan.startTimeMillis
                            if (actualAlarm1Time > System.currentTimeMillis()) {
                                val intent1 = Intent(context, AlarmReceiver::class.java).apply {
                                    putExtra("STUDY_PLAN_ID", plan.id)
                                    putExtra("ALARM_TYPE", 1)
                                }
                                val pendingIntent1 = PendingIntent.getBroadcast(context, plan.id * 10 + 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                                try {
                                    val info1 = AlarmManager.AlarmClockInfo(actualAlarm1Time, pendingIntent1)
                                    alarmManager.setAlarmClock(info1, pendingIntent1)
                                } catch (e: SecurityException) {
                                    e.printStackTrace()
                                }
                            }'''

boot_content = boot_content.replace(old_boot_alarm1, new_boot_alarm1)

with open('app/src/main/java/com/example/services/BootReceiver.kt', 'w') as f:
    f.write(boot_content)

