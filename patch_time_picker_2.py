import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Find the dialog function
    if "fun TaskEntryDialog" in content:
        # Add states for time pickers
        state_vars = """    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
"""
        
        # Replace the first mutableStateOf(initialTask?.endTimeMillis) with the new states
        content = re.sub(r'(var endTime by remember \{ mutableStateOf\(initialTask\?\.endTimeMillis\) \})', r'\1\n' + state_vars, content)
        
        pickers_code = """
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
"""
        
        old_start_time_picker = """                        TextButton(onClick = {
                            val cal = Calendar.getInstance()
                            if (startTime != null) cal.timeInMillis = startTime!!
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val newCal = Calendar.getInstance().apply {
                                        timeInMillis = dateMillis
                                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        set(Calendar.MINUTE, minute)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    startTime = newCal.timeInMillis
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                false
                            ).show()
                        })"""
        new_start_time_picker = """                        TextButton(onClick = { showStartTimePicker = true })"""
        content = content.replace(old_start_time_picker, new_start_time_picker)

        old_end_time_picker = """                        TextButton(onClick = {
                            val cal = Calendar.getInstance()
                            if (endTime != null) cal.timeInMillis = endTime!!
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val newCal = Calendar.getInstance().apply {
                                        timeInMillis = dateMillis
                                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        set(Calendar.MINUTE, minute)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    endTime = newCal.timeInMillis
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                false
                            ).show()
                        })"""
        new_end_time_picker = """                        TextButton(onClick = { showEndTimePicker = true })"""
        content = content.replace(old_end_time_picker, new_end_time_picker)

        content = content.rsplit('}', 1)[0] + pickers_code + '\n}'

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt')
