import re

with open('app/src/main/java/com/example/services/AlarmReceiver.kt', 'r') as f:
    content = f.read()

# Replace showNotification signature and call sites
# First, the StudyPlan call site
content = content.replace(
    'showNotification(context, planId, "Study Time: ${plan.subject}", plan.chapter, alarmType, true)',
    '''val endTime = plan.startTimeMillis + (plan.targetDurationMinutes * 60 * 1000L)
                        showNotification(context, planId, "Study Time: ${plan.subject}", plan.chapter, alarmType, true, endTime)'''
)

# Then the Task call site
content = content.replace(
    'showNotification(context, taskId + 10000, "Task Reminder", task.title, 1, false)',
    '''val endTime = task.endTimeMillis ?: (task.startTimeMillis ?: 0L)
                        showNotification(context, taskId + 10000, "Task Reminder", task.title, 1, false, endTime)'''
)

# Then the method signature
content = content.replace(
    'private fun showNotification(context: Context, notifId: Int, title: String, message: String, alarmType: Int, isStudyPlan: Boolean) {',
    'private fun showNotification(context: Context, notifId: Int, title: String, message: String, alarmType: Int, isStudyPlan: Boolean, endTimeMillis: Long) {'
)

# Then the Intent setup
content = content.replace(
    'putExtra("IS_STUDY_PLAN", isStudyPlan)',
    'putExtra("IS_STUDY_PLAN", isStudyPlan)\n            putExtra("END_TIME_MILLIS", endTimeMillis)'
)

with open('app/src/main/java/com/example/services/AlarmReceiver.kt', 'w') as f:
    f.write(content)
