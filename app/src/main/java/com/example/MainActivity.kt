package com.example

import com.google.android.gms.ads.MobileAds
import android.provider.Settings
import android.net.Uri
import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.Manifest
import android.os.Build
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.utils.LocaleManager
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.repository.PlannerRepository
import com.example.services.AlarmService
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = Uri.parse("package:$packageName")
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        
        val db = AppDatabase.getDatabase(this)
        val repository = PlannerRepository(db.taskDao(), db.subjectDao(), db.chapterDao(), db.studySessionDao(), db.studyPlanDao())

        
        if (intent?.getBooleanExtra("STOP_ALARM_SERVICE", false) == true) {
            val stopIntent = Intent(this, AlarmService::class.java).apply {
                action = "STOP_ALARM"
            }
            startService(stopIntent)
        }
        
        var startPlanId: Int? = null
        if (intent?.hasExtra("START_STUDY_PLAN_ID") == true) {
            val planId = intent.getIntExtra("START_STUDY_PLAN_ID", -1)
            if (planId != -1) {
                startPlanId = planId
                CoroutineScope(Dispatchers.IO).launch {
                    val plan = db.studyPlanDao().getStudyPlanById(planId)
                    if (plan != null) {
                        db.studyPlanDao().updateStudyPlan(plan.copy(inProgress = true, status = "In Progress"))
                    }
                }
            }
        }
        
        enableEdgeToEdge()
        
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
                        AppNavigation(repository, startPlanId)
                    }
                }
            }
        }
    }
}
