package com.example.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.AlarmManager
import android.app.PendingIntent
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    
                    // Reschedule Study Plans
                    val plans = db.studyPlanDao().getAllStudyPlansSync()
                    for (plan in plans) {
                        if (!plan.isCompleted && !plan.inProgress) {
                            val actualAlarm1Time = plan.alarm1TimeMillis ?: plan.startTimeMillis
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
                            }
                            
                            if (plan.alarm2TimeMillis != null && plan.alarm2TimeMillis > System.currentTimeMillis()) {
                                val intent2 = Intent(context, AlarmReceiver::class.java).apply {
                                    putExtra("STUDY_PLAN_ID", plan.id)
                                    putExtra("ALARM_TYPE", 2)
                                }
                                val pendingIntent2 = PendingIntent.getBroadcast(context, plan.id * 10 + 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                                try {
                                    val info2 = AlarmManager.AlarmClockInfo(plan.alarm2TimeMillis, pendingIntent2)
                                    alarmManager.setAlarmClock(info2, pendingIntent2)
                                } catch (e: SecurityException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                    
                    // Reschedule Tasks
                    val tasks = db.taskDao().getAllTasksSync()
                    for (task in tasks) {
                        if (!task.isCompleted && task.reminderTimeMillis != null && task.reminderTimeMillis > System.currentTimeMillis()) {
                            val taskIntent = Intent(context, AlarmReceiver::class.java).apply {
                                putExtra("TASK_ID", task.id)
                            }
                            val taskPendingIntent = PendingIntent.getBroadcast(context, task.id * 10, taskIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                            try {
                                val info = AlarmManager.AlarmClockInfo(task.reminderTimeMillis, taskPendingIntent)
                                alarmManager.setAlarmClock(info, taskPendingIntent)
                            } catch (e: SecurityException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
