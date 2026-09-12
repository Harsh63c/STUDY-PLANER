import re

with open('app/src/main/java/com/example/services/AlarmReceiver.kt', 'r') as f:
    content = f.read()

new_on_receive = """    override fun onReceive(context: Context, intent: Intent) {
        val planId = intent.getIntExtra("STUDY_PLAN_ID", -1)
        val taskId = intent.getIntExtra("TASK_ID", -1)
        val alarmType = intent.getIntExtra("ALARM_TYPE", 1) // 1 or 2
        
        val pendingResult = goAsync()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                
                if (planId != -1) {
                    val plan = db.studyPlanDao().getStudyPlanById(planId)
                    if (plan != null) {
                        if (alarmType == 2 && (plan.inProgress || plan.isCompleted)) {
                            return@launch
                        }
                        if (alarmType == 1) {
                            db.studyPlanDao().updateStudyPlan(plan.copy(status = "Alarm 1 Triggered"))
                        }
                        showNotification(context, planId, "Study Time: ${plan.subject}", plan.chapter, alarmType, true)
                    }
                } else if (taskId != -1) {
                    val task = db.taskDao().getTaskById(taskId)
                    if (task != null && !task.isCompleted) {
                        showNotification(context, taskId + 10000, "Task Reminder", task.title, 1, false)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }"""

content = re.sub(r'    override fun onReceive.*?    \}\s*\n\s*    private fun showNotification', new_on_receive + '\n    \n    private fun showNotification', content, flags=re.DOTALL)

with open('app/src/main/java/com/example/services/AlarmReceiver.kt', 'w') as f:
    f.write(content)
