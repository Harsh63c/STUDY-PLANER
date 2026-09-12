with open("app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.text.font.FontWeight", "import androidx.compose.ui.text.font.FontWeight\nimport androidx.compose.ui.res.stringResource\nimport com.example.R")

replace_map = {
    '"Add Task"': 'stringResource(R.string.add_task)',
    '"Edit Task"': 'stringResource(R.string.edit_task)',
    '"Task Title"': 'stringResource(R.string.task_title)',
    '"Delete Task"': 'stringResource(R.string.delete_task)',
    '"No tasks scheduled for this month."': 'stringResource(R.string.no_tasks_month)',
    '"Time: ${timeFormat.format(startTime)}"': 'stringResource(R.string.time_study, timeFormat.format(startTime))',
    '"Cancel"': 'stringResource(R.string.cancel)',
    '"Save"': 'stringResource(R.string.save)',
    '"Delete"': 'stringResource(R.string.delete)',
    '"Not set"': 'stringResource(R.string.not_set)',
    '"Calendar"': 'stringResource(R.string.calendar)',
    '"Date: ${dateFormat.format(dateMillis)}"': 'stringResource(R.string.date, dateFormat.format(dateMillis))',
    '"Start Time: ${startTime?.let { timeFormat.format(it) } ?: "': 'stringResource(R.string.start_time, startTime?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)) + "',
    '"End Time: ${endTime?.let { timeFormat.format(it) } ?: "': 'stringResource(R.string.end_time, endTime?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)) + "',
    '"Reminder: ${reminderTime?.let { timeFormat.format(it) } ?: "': 'stringResource(R.string.reminder_time, reminderTime?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)) + "',
    '" • Repeat: ${task.repeatMode}"': '(" • " + stringResource(R.string.repeat_mode, task.repeatMode))',
    '" (Next Skipped)"': '(" " + stringResource(R.string.next_skipped))',
    '"Skip Next Occurrence"': 'stringResource(R.string.skip_next)',
    '"Repeat Schedule"': 'stringResource(R.string.repeat_schedule)',
    '"Select Days:"': 'stringResource(R.string.select_days)',
    '"None"': 'stringResource(R.string.none)',
    '"Daily"': 'stringResource(R.string.daily)',
    '"Weekdays"': 'stringResource(R.string.weekdays)',
    '"Weekends"': 'stringResource(R.string.weekends)',
    '"Custom"': 'stringResource(R.string.custom)',
    '"S"': 'stringResource(R.string.day_s)',
    '"M"': 'stringResource(R.string.day_m)',
    '"T"': 'stringResource(R.string.day_t)',
    '"W"': 'stringResource(R.string.day_w)',
    '"F"': 'stringResource(R.string.day_f)',
    '"Next Month"': 'stringResource(R.string.next_month)',
    '"Previous Month"': 'stringResource(R.string.previous_month)',
}

# we need to be careful with string interpolation replacements
for k, v in replace_map.items():
    content = content.replace(k, v)

with open("app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt", "w") as f:
    f.write(content)
