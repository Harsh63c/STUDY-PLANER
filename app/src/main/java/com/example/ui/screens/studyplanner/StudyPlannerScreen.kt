package com.example.ui.screens.studyplanner

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.StudyPlan
import com.example.services.AlarmReceiver
import com.example.viewmodel.StudyPlannerViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.TimePickerDialog
import com.example.ui.components.WheelTimePickerDialog
import android.app.DatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlannerScreen(viewModel: StudyPlannerViewModel, onNavigateToTimer: (Int, Int, Int?) -> Unit, onNavigateToFocusLock: () -> Unit) {
    val selectedDate by viewModel.selectedDateMillis.collectAsStateWithLifecycle()
    val currentMonth by viewModel.currentMonthMillis.collectAsStateWithLifecycle()
    val studyPlanDates by viewModel.studyPlanDatesForMonth.collectAsStateWithLifecycle()
    val studyPlansForMonth by viewModel.studyPlansForMonth.collectAsStateWithLifecycle()

    var showPlanDialog by remember { mutableStateOf(false) }
    var planToEdit by remember { mutableStateOf<StudyPlan?>(null) }
    
    val context = LocalContext.current

    fun scheduleAlarms(plan: StudyPlan) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Cancel existing alarms if any
        val intent1 = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("STUDY_PLAN_ID", plan.id)
            putExtra("ALARM_TYPE", 1)
        }
        val pendingIntent1 = PendingIntent.getBroadcast(context, plan.id * 10 + 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent1)
        
        val intent2 = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("STUDY_PLAN_ID", plan.id)
            putExtra("ALARM_TYPE", 2)
        }
        val pendingIntent2 = PendingIntent.getBroadcast(context, plan.id * 10 + 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent2)
        
        val actualAlarm1Time = plan.alarm1TimeMillis ?: plan.startTimeMillis
        if (actualAlarm1Time > System.currentTimeMillis() && !plan.inProgress && !plan.isCompleted) {
            try {
                val info1 = AlarmManager.AlarmClockInfo(actualAlarm1Time, pendingIntent1)
                alarmManager.setAlarmClock(info1, pendingIntent1)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        
        if (plan.alarm2TimeMillis != null && plan.alarm2TimeMillis > System.currentTimeMillis() && !plan.inProgress && !plan.isCompleted) {
            try {
                val info2 = AlarmManager.AlarmClockInfo(plan.alarm2TimeMillis, pendingIntent2)
                alarmManager.setAlarmClock(info2, pendingIntent2)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
    
    fun cancelAlarms(plan: StudyPlan) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent1 = Intent(context, AlarmReceiver::class.java)
        val pendingIntent1 = PendingIntent.getBroadcast(context, plan.id * 10 + 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent1)
        val intent2 = Intent(context, AlarmReceiver::class.java)
        val pendingIntent2 = PendingIntent.getBroadcast(context, plan.id * 10 + 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent2)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.study_planner)) },
                actions = {
                    IconButton(onClick = onNavigateToFocusLock) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.focus_lock)) // Ideally a shield or lock icon
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            StudyCalendarGrid(
                currentMonthMillis = currentMonth,
                selectedDateMillis = selectedDate,
                studyPlanDates = studyPlanDates,
                onDateSelected = { viewModel.selectDate(it) },
                onMonthChanged = { viewModel.changeMonth(it) }
            )
            
            Divider(modifier = Modifier.padding(top = 8.dp))
            
            val selectedDateFormat = remember { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedDateFormat.format(selectedDate),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = { 
                    planToEdit = null
                    showPlanDialog = true 
                }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_study_plan), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.add_study_plan))
                }
            }

            Divider(modifier = Modifier.padding(bottom = 8.dp))

            if (studyPlansForMonth.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_plans_month), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val groupedPlans = remember(studyPlansForMonth) {
                    studyPlansForMonth.groupBy { it.dateMillis }.toSortedMap()
                }
                val headerDateFormat = remember { SimpleDateFormat("MMMM d", Locale.getDefault()) }
                
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    groupedPlans.forEach { (dateMillis, plansOnDate) ->
                        item(key = "header_$dateMillis") {
                            Text(
                                text = headerDateFormat.format(dateMillis),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                            )
                        }
                        items(
                            items = plansOnDate.sortedBy { it.startTimeMillis },
                            key = { it.id }
                        ) { plan ->
                            StudyPlanItem(
                                plan = plan,
                                onToggleCompletion = { 
                                    val updated = plan.copy(isCompleted = !plan.isCompleted)
                                    viewModel.updateStudyPlan(updated)
                                    scheduleAlarms(updated)
                                },
                                onEdit = {
                                    planToEdit = plan
                                    showPlanDialog = true
                                },
                                onDelete = { 
                                    cancelAlarms(plan)
                                    viewModel.deleteStudyPlan(plan) 
                                },
                                onStartStudy = {
                                    val updated = plan.copy(inProgress = true, status = "In Progress")
                                    viewModel.updateStudyPlan(updated)
                                    cancelAlarms(updated) // Cancel alarm 2
                                    // Navigate to timer - pass fake ids for now or subject id
                                    onNavigateToTimer(1, 1, plan.id) 
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showPlanDialog) {
            StudyPlanEntryDialog(
                initialPlan = planToEdit,
                defaultDateMillis = selectedDate,
                onDismiss = { showPlanDialog = false },
                onSave = { plan ->
                    if (plan.id == 0) {
                        viewModel.addStudyPlan(plan) { insertedPlan -> scheduleAlarms(insertedPlan) }
                    } else {
                        viewModel.updateStudyPlan(plan)
                        scheduleAlarms(plan)
                    }
                    showPlanDialog = false
                }
            )
        }
    }
}

@Composable
fun StudyCalendarGrid(
    currentMonthMillis: Long,
    selectedDateMillis: Long,
    studyPlanDates: Set<Long>,
    onDateSelected: (Long) -> Unit,
    onMonthChanged: (Int) -> Unit
) {
    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val currentMonthText = monthFormat.format(currentMonthMillis)

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(36.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(-1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
            }
            Text(text = currentMonthText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { onMonthChanged(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val daysOfWeek = listOf(stringResource(R.string.day_s), stringResource(R.string.day_m), stringResource(R.string.day_t), stringResource(R.string.day_w), stringResource(R.string.day_t), stringResource(R.string.day_f), stringResource(R.string.day_s))
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val cal = remember { Calendar.getInstance() }.apply { timeInMillis = currentMonthMillis }
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 

        var dayCounter = 1
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = Math.ceil(totalCells / 7.0).toInt()

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    if (cellIndex >= firstDayOfWeek && dayCounter <= daysInMonth) {
                        cal.set(Calendar.DAY_OF_MONTH, dayCounter)
                        val dateMillis = cal.timeInMillis
                        val isSelected = dateMillis == selectedDateMillis
                        val hasStudyPlan = studyPlanDates.contains(dateMillis)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { onDateSelected(dateMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayCounter.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                if (hasStudyPlan) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Blue))
                                }
                            }
                        }
                        dayCounter++
                    } else {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun StudyPlanItem(
    plan: StudyPlan,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onStartStudy: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = plan.isCompleted,
                onCheckedChange = { onToggleCompletion() },
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.subject,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (plan.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = plan.chapter,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                
                val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val dateString = dateFormat.format(plan.dateMillis)
                val timeString = timeFormat.format(plan.startTimeMillis)
                
                Text(
                    text = "$dateString • $timeString • Target: ${plan.targetDurationMinutes}m • Repeat: Once",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                IconButton(onClick = onStartStudy, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.start_study), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_plan), tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_plan), tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlanEntryDialog(
    initialPlan: StudyPlan?,
    defaultDateMillis: Long,
    onDismiss: () -> Unit,
    onSave: (StudyPlan) -> Unit
) {
    var subject by remember { mutableStateOf(initialPlan?.subject ?: "") }
    var chapter by remember { mutableStateOf(initialPlan?.chapter ?: "") }
    var dateMillis by remember { mutableStateOf(initialPlan?.dateMillis ?: defaultDateMillis) }
    var startTime by remember { mutableStateOf(initialPlan?.startTimeMillis ?: defaultDateMillis) }
    var targetDuration by remember { mutableStateOf(initialPlan?.targetDurationMinutes?.toString() ?: "60") }
    var notes by remember { mutableStateOf(initialPlan?.notes ?: "") }
    var alarm1Time by remember { mutableStateOf(initialPlan?.alarm1TimeMillis) }
    var alarm2Time by remember { mutableStateOf(initialPlan?.alarm2TimeMillis) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showAlarm1Picker by remember { mutableStateOf(false) }
        var showAlarm2Picker by remember { mutableStateOf(false) }
    var repeatMode by remember { mutableStateOf(initialPlan?.repeatMode ?: "None") }
    var customRepeatDays by remember { mutableStateOf(initialPlan?.customRepeatDays ?: "") }
    var skipNextOccurrence by remember { mutableStateOf(initialPlan?.skipNextOccurrence ?: false) }


    
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialPlan == null) stringResource(R.string.add_study_plan) else stringResource(R.string.edit_study_plan)) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text(stringResource(R.string.subject)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = chapter,
                        onValueChange = { chapter = it },
                        label = { Text(stringResource(R.string.chapter_topic)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = targetDuration,
                        onValueChange = { targetDuration = it },
                        label = { Text(stringResource(R.string.target_duration)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.date, dateFormat.format(dateMillis)))
                        TextButton(onClick = {
                            val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val newCal = Calendar.getInstance().apply {
                                        set(year, month, dayOfMonth, 0, 0, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    val oldCal = Calendar.getInstance().apply { timeInMillis = dateMillis }
                                    dateMillis = newCal.timeInMillis
                                    val updateTime = { timeMillis: Long? ->
                                        if (timeMillis != null) {
                                            val tCal = Calendar.getInstance().apply { timeInMillis = timeMillis }
                                            tCal.set(Calendar.YEAR, newCal.get(Calendar.YEAR))
                                            tCal.set(Calendar.MONTH, newCal.get(Calendar.MONTH))
                                            tCal.set(Calendar.DAY_OF_MONTH, newCal.get(Calendar.DAY_OF_MONTH))
                                            tCal.timeInMillis
                                        } else null
                                    }
                                    startTime = updateTime(startTime) ?: dateMillis
                                    alarm1Time = updateTime(alarm1Time)
                                    alarm2Time = updateTime(alarm2Time)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }) {
                            Text(stringResource(R.string.change))
                        }
                    }
                }
                
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Time: ${timeFormat.format(startTime)}")
                        TextButton(onClick = {
                            val cal = Calendar.getInstance().apply { timeInMillis = startTime }
showStartTimePicker = true
                        }) {
                            Text(stringResource(R.string.change))
                        }
                    }
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Alarm 1: " + (alarm1Time?.let { timeFormat.format(it) } ?: "Not set"))
                        TextButton(onClick = {
                            val cal = Calendar.getInstance()
                            if (alarm1Time != null) cal.timeInMillis = alarm1Time!!
showAlarm1Picker = true
                        }) {
                            Text(stringResource(R.string.change))
                        }
                    }
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Alarm 2: " + (alarm2Time?.let { timeFormat.format(it) } ?: "Not set"))
                        TextButton(onClick = {
                            val cal = Calendar.getInstance()
                            if (alarm2Time != null) cal.timeInMillis = alarm2Time!!
showAlarm2Picker = true
                        }) {
                            Text(stringResource(R.string.change))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (subject.isNotBlank() && chapter.isNotBlank()) {
                        val duration = targetDuration.toIntOrNull() ?: 60
                        val newPlan = initialPlan?.copy(
                            subject = subject,
                            chapter = chapter,
                            dateMillis = dateMillis,
                            startTimeMillis = startTime,
                            targetDurationMinutes = duration,
                            notes = notes,
                            alarm1TimeMillis = alarm1Time,
                            alarm2TimeMillis = alarm2Time
                        ) ?: StudyPlan(
                            subject = subject,
                            chapter = chapter,
                            dateMillis = dateMillis,
                            startTimeMillis = startTime,
                            targetDurationMinutes = duration,
                            notes = notes,
                            alarm1TimeMillis = alarm1Time,
                            alarm2TimeMillis = alarm2Time
                        )
                        onSave(newPlan)
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (showStartTimePicker) {
        val cal = Calendar.getInstance().apply { timeInMillis = startTime }
        WheelTimePickerDialog(
            initialHourOfDay = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            onDismissRequest = { showStartTimePicker = false },
            onTimeSelected = { hourOfDay, minute ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = dateMillis
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                startTime = newCal.timeInMillis
                showStartTimePicker = false
            }
        )
    }

    if (showAlarm1Picker) {
        val cal = Calendar.getInstance()
        if (alarm1Time != null) cal.timeInMillis = alarm1Time!!
        WheelTimePickerDialog(
            initialHourOfDay = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            onDismissRequest = { showAlarm1Picker = false },
            onTimeSelected = { hourOfDay, minute ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = dateMillis
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                alarm1Time = newCal.timeInMillis
                showAlarm1Picker = false
            }
        )
    }

    if (showAlarm2Picker) {
        val cal = Calendar.getInstance()
        if (alarm2Time != null) cal.timeInMillis = alarm2Time!!
        WheelTimePickerDialog(
            initialHourOfDay = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            onDismissRequest = { showAlarm2Picker = false },
            onTimeSelected = { hourOfDay, minute ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = dateMillis
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                alarm2Time = newCal.timeInMillis
                showAlarm2Picker = false
            }
        )
    }

}