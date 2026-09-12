package com.example.ui.screens.studyplanner

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.os.Build
import com.example.services.FocusLockService
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.viewmodel.StudyPlannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(viewModel: StudyPlannerViewModel, subjectId: Int, chapterId: Int, studyPlanId: Int? = null, onNavigateBack: () -> Unit) {
    var isRunning by remember { mutableStateOf(false) }
    var secondsElapsed by remember { mutableStateOf(0) }
    val context = LocalContext.current

    LaunchedEffect(isRunning) {
        if (isRunning) {
            val intent = Intent(context, FocusLockService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            val intent = Intent(context, FocusLockService::class.java)
            context.stopService(intent)
        }

        while (isRunning) {
            delay(1000L)
            secondsElapsed++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.study_timer)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val hours = secondsElapsed / 3600
            val minutes = (secondsElapsed % 3600) / 60
            val seconds = secondsElapsed % 60
            Text(
                text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { isRunning = !isRunning }) {
                    Text(if (isRunning) "Pause" else "Start")
                }
                Button(
                    onClick = { 
                        isRunning = false
                        viewModel.saveStudySession(
                            subjectId = subjectId,
                            chapterId = chapterId,
                            durationMinutes = if (secondsElapsed > 60) secondsElapsed / 60 else 1,
                            studyPlanId = studyPlanId
                        )
                        secondsElapsed = 0
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.stop_and_save))
                }
            }
        }
    }
}
