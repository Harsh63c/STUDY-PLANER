package com.example.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
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
                        val endTime = plan.startTimeMillis + (plan.targetDurationMinutes * 60 * 1000L)
                        showNotification(context, planId, "Study Time: ${plan.subject}", plan.chapter, alarmType, true, endTime)
                    }
                } else if (taskId != -1) {
                    val task = db.taskDao().getTaskById(taskId)
                    if (task != null && !task.isCompleted) {
                        val endTime = task.endTimeMillis ?: (task.startTimeMillis ?: 0L)
                        showNotification(context, taskId + 10000, "Task Reminder", task.title, 1, false, endTime)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
    
    private fun showNotification(context: Context, notifId: Int, title: String, message: String, alarmType: Int, isStudyPlan: Boolean, endTimeMillis: Long) {
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("TITLE", title)
            putExtra("MESSAGE", message)
            putExtra("NOTIF_ID", notifId)
            putExtra("IS_STUDY_PLAN", isStudyPlan)
            putExtra("END_TIME_MILLIS", endTimeMillis)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
