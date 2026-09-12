package com.example.ui.screens.studyplanner

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.utils.LocaleManager

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.services.AlarmReceiver
import com.example.services.AlarmService
import com.example.utils.SettingsPreferences
import com.example.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmRingingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Wake up screen and show when locked
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val title = intent.getStringExtra("TITLE") ?: "Alarm"
        val message = intent.getStringExtra("MESSAGE") ?: "Time to study!"
        val notifId = intent.getIntExtra("NOTIF_ID", 1)
        val isStudyPlan = intent.getBooleanExtra("IS_STUDY_PLAN", false)
        
        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

                val snoozeDuration = SettingsPreferences.getSnoozeDuration(this)
                LocaleManager.init(this)
        setContent {
            val languageCode by LocaleManager.currentLanguage.collectAsState()
            val localizedContext = LocaleManager.getLocalizedContext(this, languageCode)
            val configuration = localizedContext.resources.configuration

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides configuration
            ) {
                MyApplicationTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AlarmScreenContent(
                            title = title,
                            message = message,
                            timeString = currentTime,
                            snoozeDuration = snoozeDuration,
                            onDismiss = {
                                dismissAlarm()
                                finish()
                            },
                            onSnooze = {
                                snoozeAlarm(title, message, notifId, isStudyPlan, snoozeDuration)
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun dismissAlarm() {
        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = "STOP_ALARM"
        }
        startService(stopIntent)
    }

    private fun snoozeAlarm(title: String, message: String, notifId: Int, isStudyPlan: Boolean, durationMinutes: Int = 5) {
        // Stop current ringing
        dismissAlarm()
        
        // Schedule snooze alarm 5 minutes from now
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val snoozeTimeMillis = System.currentTimeMillis() + (durationMinutes * 60 * 1000)
        
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            if (isStudyPlan) {
                putExtra("STUDY_PLAN_ID", notifId)
                putExtra("ALARM_TYPE", 1) // Default to 1, or pass along proper type
            } else {
                putExtra("TASK_ID", notifId - 10000)
            }
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            this, 
            notifId * 10 + 99, // Unique snooze ID
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val alarmClockInfo = AlarmManager.AlarmClockInfo(snoozeTimeMillis, pendingIntent)
        try {
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

@Composable
fun AlarmScreenContent(
    title: String,
    message: String,
    timeString: String,
    snoozeDuration: Int,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bell_anim")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bell_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.NotificationsActive,
                    contentDescription = "Ringing Bell",
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = timeString,
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.dismiss), style = MaterialTheme.typography.headlineSmall)
            }
            
            OutlinedButton(
                onClick = onSnooze,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.snooze, snoozeDuration.toString()), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
