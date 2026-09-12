package com.example.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import android.os.Build
import android.os.PowerManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.ui.screens.studyplanner.AlarmRingingActivity
import com.example.utils.SettingsPreferences
import android.app.AlarmManager

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var countdownJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_ALARM") {
            stopSelf()
            return START_NOT_STICKY
        }
        
        val title = intent?.getStringExtra("TITLE") ?: "Alarm"
        val message = intent?.getStringExtra("MESSAGE") ?: "Time to study!"
        val notifId = intent?.getIntExtra("NOTIF_ID", 1) ?: 1
        val isStudyPlan = intent?.getBooleanExtra("IS_STUDY_PLAN", false) ?: false
        
        if (intent?.action == "SNOOZE_ALARM") {
            val snoozeDuration = SettingsPreferences.getSnoozeDuration(this)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val snoozeTimeMillis = System.currentTimeMillis() + (snoozeDuration * 60 * 1000)
            val snoozeIntent = Intent(this, com.example.services.AlarmReceiver::class.java).apply {
                if (isStudyPlan) {
                    putExtra("STUDY_PLAN_ID", notifId)
                    putExtra("ALARM_TYPE", 1)
                } else {
                    putExtra("TASK_ID", notifId - 10000)
                }
            }
            val pendingIntent = PendingIntent.getBroadcast(
                this, 
                notifId * 10 + 99, 
                snoozeIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmClockInfo = AlarmManager.AlarmClockInfo(snoozeTimeMillis, pendingIntent)
            try {
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            stopSelf()
            return START_NOT_STICKY
        }


        val endTimeMillis = intent?.getLongExtra("END_TIME_MILLIS", 0L) ?: 0L
        startForegroundAlarm(notifId, title, message, isStudyPlan, endTimeMillis)
        playAlarmSound()

        return START_STICKY
    }

    private fun startForegroundAlarm(notifId: Int, title: String, message: String, isStudyPlan: Boolean, endTimeMillis: Long) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "study_planner_alarms_v3"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Study Planner Alarms", NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(null, null) // Sound is handled by MediaPlayer
            notificationManager.createNotificationChannel(channel)
        }

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (isStudyPlan) {
                putExtra("START_STUDY_PLAN_ID", notifId)
            }
            putExtra("STOP_ALARM_SERVICE", true)
        }
        val pendingIntent = PendingIntent.getActivity(this, notifId, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = "STOP_ALARM"
        }
        val stopPendingIntent = PendingIntent.getService(this, notifId + 1000, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val deletePendingIntent = PendingIntent.getService(this, notifId + 4000, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val fullScreenIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            putExtra("TITLE", title)
            putExtra("MESSAGE", message)
            putExtra("NOTIF_ID", notifId)
            putExtra("IS_STUDY_PLAN", isStudyPlan)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(this, notifId + 3000, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        
        val snoozeDuration = SettingsPreferences.getSnoozeDuration(this)
        val snoozeIntent = Intent(this, AlarmService::class.java).apply {
            action = "SNOOZE_ALARM"
            putExtra("TITLE", title)
            putExtra("MESSAGE", message)
            putExtra("NOTIF_ID", notifId)
            putExtra("IS_STUDY_PLAN", isStudyPlan)
        }
        val snoozePendingIntent = PendingIntent.getService(this, notifId + 5000, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(deletePendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(android.R.drawable.ic_popup_sync, "Snooze (${snoozeDuration}m)", snoozePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", stopPendingIntent)


        if (isStudyPlan) {
            val startStudyIntent = Intent(this, MainActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("START_STUDY_PLAN_ID", notifId)
                putExtra("STOP_ALARM_SERVICE", true)
            }
            val startStudyPendingIntent = PendingIntent.getActivity(this, notifId + 2000, startStudyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            builder.addAction(android.R.drawable.ic_media_play, "Start Study", startStudyPendingIntent)
        }

        
        if (endTimeMillis > System.currentTimeMillis() && !isStudyPlan) {
            builder.setUsesChronometer(true)
            builder.setWhen(endTimeMillis)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setChronometerCountDown(true)
            }
        }
        
        startForeground(notifId, builder.build())
        
        if (endTimeMillis > System.currentTimeMillis() && isStudyPlan) {
            startCountdown(notifId, builder, notificationManager, endTimeMillis)
        }
    }
    
    private fun startCountdown(notifId: Int, builder: NotificationCompat.Builder, notificationManager: NotificationManager, endTimeMillis: Long) {
        countdownJob?.cancel()
        countdownJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                if (now >= endTimeMillis) {
                    builder.setContentTitle("Study Alarm 🔔 — 00:00 remaining")
                    notificationManager.notify(notifId, builder.build())
                    break
                }
                
                val remainingMillis = endTimeMillis - now
                val minutes = remainingMillis / (60 * 1000)
                val seconds = (remainingMillis % (60 * 1000)) / 1000
                val timeString = String.format("%02d:%02d", minutes, seconds)
                
                builder.setContentTitle("Study Alarm 🔔 — $timeString remaining")
                notificationManager.notify(notifId, builder.build())
                
                delay(1000)
            }
        }
    }

    private fun playAlarmSound() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
            return
        }
        try {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, alarmSound)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setAudioAttributes(audioAttributes)
                isLooping = true
                setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownJob?.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaPlayer = null
    }
}
