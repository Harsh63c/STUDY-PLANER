files = ["app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt"]

for file in files:
    with open(file, "r") as f:
        content = f.read()

    content = content.replace('stringResource(R.string.date, dateFormat.format(dateMillis))', '"Date: ${dateFormat.format(dateMillis)}"')
    content = content.replace('stringResource(R.string.time_study, timeFormat.format(startTime))', '"Time: ${timeFormat.format(startTime)}"')
    content = content.replace('stringResource(R.string.start_time, startTime?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)) + "', 'Start Time: ${startTime?.let { timeFormat.format(it) } ?: "Not set"}"')
    content = content.replace('stringResource(R.string.end_time, endTime?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)) + "', 'End Time: ${endTime?.let { timeFormat.format(it) } ?: "Not set"}"')
    content = content.replace('stringResource(R.string.reminder_time, reminderTime?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)) + "', 'Reminder: ${reminderTime?.let { timeFormat.format(it) } ?: "Not set"}"')
    content = content.replace('(" • " + stringResource(R.string.repeat_mode, task.repeatMode))', '" • Repeat: ${task.repeatMode}"')

    with open(file, "w") as f:
        f.write(content)
