package com.example.ui.screens.calendar
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.services.AlarmReceiver


import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import com.example.R
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.example.ui.components.WheelTimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.Task
import com.example.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CalendarScreen(viewModel: CalendarViewModel) {
    val context = LocalContext.current
    val selectedDate by viewModel.selectedDateMillis.collectAsStateWithLifecycle()
    val currentMonth by viewModel.currentMonthMillis.collectAsStateWithLifecycle()
    val taskDates by viewModel.taskDatesForMonth.collectAsStateWithLifecycle()
    val tasksForMonth by viewModel.tasksForMonth.collectAsStateWithLifecycle()

    var showTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.calendar)) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            MonthlyCalendar(
                currentMonthMillis = currentMonth,
                selectedDateMillis = selectedDate,
                taskDates = taskDates,
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
                    taskToEdit = null
                    showTaskDialog = true 
                }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_task), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.add_task))
                }
            }

            Divider(modifier = Modifier.padding(bottom = 8.dp))

            if (tasksForMonth.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_tasks_month), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val groupedTasks = remember(tasksForMonth) {
                    tasksForMonth.groupBy { it.dateMillis }.toSortedMap()
                }
                val headerDateFormat = remember { SimpleDateFormat("MMMM d", Locale.getDefault()) }
                
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    groupedTasks.forEach { (dateMillis, tasksOnDate) ->
                        item(key = "header_$dateMillis") {
                            Text(
                                text = headerDateFormat.format(dateMillis),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(
                            items = tasksOnDate.sortedBy { it.startTimeMillis ?: Long.MAX_VALUE },
                            key = { it.id }
                        ) { task ->
                            TaskItem(
                                task = task,
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                onEdit = {
                                    taskToEdit = task
                                    showTaskDialog = true
                                },
                                onDelete = { viewModel.deleteTask(task) }
                            )
                        }
                    }
                }
            }
        }
        
        if (showTaskDialog) {
            TaskEntryDialog(
                initialTask = taskToEdit,
                defaultDateMillis = selectedDate,
                onDismiss = { 
                    showTaskDialog = false
                    taskToEdit = null
                },
                onSave = { task ->
                    if (task.id == 0) {
                        viewModel.addTask(task.title, task.startTimeMillis, task.endTimeMillis, task.reminderTimeMillis, task.dateMillis) { insertedTask -> scheduleTaskAlarm(context, insertedTask) }
                    } else {
                        viewModel.updateTask(task)
                        scheduleTaskAlarm(context, task)
                    }
                    viewModel.selectDate(task.dateMillis)
                    showTaskDialog = false
                    taskToEdit = null
                }
            )
        }
    }
}

@Composable
fun MonthlyCalendar(
    currentMonthMillis: Long,
    selectedDateMillis: Long,
    taskDates: Set<Long>,
    onDateSelected: (Long) -> Unit,
    onMonthChanged: (Int) -> Unit
) {
    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val monthTitle = monthFormat.format(currentMonthMillis)
    
    val cal = remember(currentMonthMillis) {
        Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
    }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    // Day of week starts from Sunday (1) to Saturday (7)
    // We want Monday (2) to be index 0
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val startOffset = (firstDayOfWeek + 5) % 7 // Maps Monday->0, Sunday->6
    
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(36.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(-1) }) {
                Text("<")
            }
            Text(text = monthTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { onMonthChanged(1) }) {
                Text(">")
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Days of week header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            days.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        // Calendar grid
        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7
        
        val dateCal = remember(currentMonthMillis) {
            Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
        }
        
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayOfMonth = cellIndex - startOffset + 1
                    
                    if (cellIndex >= startOffset && dayOfMonth <= daysInMonth) {
                        dateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val cellDateMillis = dateCal.timeInMillis
                        val isSelected = remember(cellDateMillis, selectedDateMillis) { 
                            isSameDay(cellDateMillis, selectedDateMillis) 
                        }
                        val hasTask = taskDates.contains(cellDateMillis)
                        val greenColor = Color(0xFF4CAF50)
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .padding(1.dp)
                                .clickable { onDateSelected(cellDateMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .then(if (hasTask) Modifier.border(2.dp, greenColor, CircleShape) else Modifier),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayOfMonth.toString(),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (hasTask) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f).height(36.dp))
                    }
                }
            }
        }
    }
}

fun isSameDay(time1: Long, time2: Long): Boolean {
    // For performance, we can just compare date boundaries or reuse calendar if we want, but since it's remembered, it's fine.
    // An even faster way:
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun TaskItem(task: Task, onToggleCompletion: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit, onSkipNext: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleCompletion, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                    contentDescription = "Toggle completion",
                    tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                
                val dateFormat = SimpleDateFormat("MMM d", java.util.Locale.getDefault())
                val timeFormat = SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                
                var details = dateFormat.format(task.dateMillis)
                if (task.startTimeMillis != null) {
                    details += " • ${timeFormat.format(task.startTimeMillis)}"
                    if (task.endTimeMillis != null) {
                        details += " - ${timeFormat.format(task.endTimeMillis)}"
                    }
                }
                
                if (task.repeatMode != "None") {
                    details += " • Repeat: ${task.repeatMode}"
                }
                if (task.repeatMode != "None") {
                    if (task.skipNextOccurrence) {
                        Text(text = (" " + stringResource(R.string.next_skipped)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    }
                }

                
                Text(
                    text = details,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                                if (task.repeatMode != "None") {
                    IconButton(onClick = onSkipNext, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.SkipNext, contentDescription = stringResource(R.string.skip_next), tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_task), tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_task), tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEntryDialog(
    initialTask: Task?,
    defaultDateMillis: Long,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var dateMillis by remember { mutableStateOf(initialTask?.dateMillis ?: defaultDateMillis) }
    var startTime by remember { mutableStateOf(initialTask?.startTimeMillis) }
    var endTime by remember { mutableStateOf(initialTask?.endTimeMillis) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showReminderTimePicker by remember { mutableStateOf(false) }

        var reminderTime by remember { mutableStateOf(initialTask?.reminderTimeMillis) }
    var repeatMode by remember { mutableStateOf(initialTask?.repeatMode ?: "None") }
    var customRepeatDays by remember { mutableStateOf(initialTask?.customRepeatDays ?: "") }
    var skipNextOccurrence by remember { mutableStateOf(initialTask?.skipNextOccurrence ?: false) }

    
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTask == null) stringResource(R.string.add_task) else stringResource(R.string.edit_task)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.task_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Date: ${dateFormat.format(dateMillis)}")
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
                                startTime = updateTime(startTime)
                                endTime = updateTime(endTime)
                                reminderTime = updateTime(reminderTime)
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Text("Change")
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Time: ${startTime?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)}")
                    TextButton(onClick = {
                        showStartTimePicker = true
                    }) {
                        Text("Change")
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("End Time: ${endTime?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)}")
                    TextButton(onClick = {
                        showEndTimePicker = true
                    }) {
                        Text("Change")
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reminder: ${reminderTime?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)}")
                    TextButton(onClick = {
                        showReminderTimePicker = true
                    }) {
                        Text("Change")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (title.isNotBlank()) {
                        val newTask = initialTask?.copy(
                            title = title,
                            dateMillis = dateMillis,
                            startTimeMillis = startTime,
                            endTimeMillis = endTime,
                            reminderTimeMillis = reminderTime
                        ) ?: Task(
                            title = title,
                            dateMillis = dateMillis,
                            startTimeMillis = startTime,
                            endTimeMillis = endTime,
                            reminderTimeMillis = reminderTime
                        )
                        onSave(newTask)
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
        val cal = Calendar.getInstance()
        if (startTime != null) cal.timeInMillis = startTime!!
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

    if (showEndTimePicker) {
        val cal = Calendar.getInstance()
        if (endTime != null) cal.timeInMillis = endTime!!
        WheelTimePickerDialog(
            initialHourOfDay = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            onDismissRequest = { showEndTimePicker = false },
            onTimeSelected = { hourOfDay, minute ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = dateMillis
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                endTime = newCal.timeInMillis
                showEndTimePicker = false
            }
        )
    }

    if (showReminderTimePicker) {
        val cal = Calendar.getInstance()
        if (reminderTime != null) cal.timeInMillis = reminderTime!!
        WheelTimePickerDialog(
            initialHourOfDay = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            onDismissRequest = { showReminderTimePicker = false },
            onTimeSelected = { hourOfDay, minute ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = dateMillis
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                reminderTime = newCal.timeInMillis
                showReminderTimePicker = false
            }
        )
    }
}

fun scheduleTaskAlarm(context: Context, task: com.example.data.models.Task) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("TASK_ID", task.id)
    }
    val pendingIntent = PendingIntent.getBroadcast(context, task.id * 10, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    alarmManager.cancel(pendingIntent)
     if (task.reminderTimeMillis != null && task.reminderTimeMillis > System.currentTimeMillis() && !task.isCompleted) {
        try {
            val info = AlarmManager.AlarmClockInfo(task.reminderTimeMillis, pendingIntent)
            alarmManager.setAlarmClock(info, pendingIntent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
