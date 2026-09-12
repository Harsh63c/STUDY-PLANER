package com.example.ui.screens.studyplanner

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.services.FocusLockService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusLockSetupScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var showWarningDialog by remember { mutableStateOf(false) }
    
    // Check if service is running
    var isServiceRunning by remember { 
        mutableStateOf(isServiceRunning(context, FocusLockService::class.java)) 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.focus_lock_setup)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.focus_lock_help), style = MaterialTheme.typography.bodyLarge)
            
            Button(onClick = { 
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }) {
                Text(stringResource(R.string.grant_usage_access))
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (isServiceRunning) {
                Button(
                    onClick = { 
                        context.stopService(Intent(context, FocusLockService::class.java))
                        isServiceRunning = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.cancel_focus_session))
                }
            } else {
                Button(
                    onClick = { showWarningDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.start_focus_lock_min))
                }
            }
        }
        
        if (showWarningDialog) {
            AlertDialog(
                onDismissRequest = { showWarningDialog = false },
                title = { Text(stringResource(R.string.start_focus_lock_prompt)) },
                text = { Text(stringResource(R.string.focus_lock_warning)) },
                confirmButton = {
                    Button(onClick = { 
                        showWarningDialog = false
                        val intent = Intent(context, FocusLockService::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                        isServiceRunning = true
                    }) {
                        Text(stringResource(R.string.i_agree_start))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWarningDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Suppress("DEPRECATION")
private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}
