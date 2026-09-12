import re

def patch_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Add import for WheelTimePickerDialog
    if 'import com.example.ui.components.WheelTimePickerDialog' not in content:
        content = content.replace('import android.app.TimePickerDialog', 'import android.app.TimePickerDialog\nimport com.example.ui.components.WheelTimePickerDialog')

    # Find the dialog function
    if "fun StudyPlanEntryDialog" in content or "fun CalendarPlanEntryDialog" in content or "fun PlanEntryDialog" in content:
        # Add states for time pickers
        state_vars = """    var showStartTimePicker by remember { mutableStateOf(false) }
    var showAlarm1Picker by remember { mutableStateOf(false) }
    var showAlarm2Picker by remember { mutableStateOf(false) }
"""
        
        # Replace the first mutableStateOf(initialPlan?.alarm2TimeMillis) with the new states
        content = re.sub(r'(var alarm2Time by remember \{ mutableStateOf\(initialPlan\?\.alarm2TimeMillis\) \})', r'\1\n' + state_vars, content)
        
        # We need to also add the WheelTimePickerDialogs at the end of the dialog, right before or after AlertDialog
        # Let's put them inside the main function, outside AlertDialog if possible. 
        # But AlertDialog is returned by StudyPlanEntryDialog. We can wrap AlertDialog in a Box, or just put them after AlertDialog since it's just composables.
        # Wait, the best place is inside the dialog function, wrapping or alongside AlertDialog.
        # Better: just use `if (show...) WheelTimePickerDialog(...)` after AlertDialog
        
        pickers_code = """
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
"""
        # We need to find the end of the AlertDialog to append pickers_code.
        # We can also just put them at the beginning of the function and return Box { AlertDialog... pickers... }
        # Or replace the TimePickerDialogs in the onClick blocks.
        
        # Replace the TimePickerDialogs with state toggles
        # For startTime
        old_start_time_picker = """                            TimePickerDialog(
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
                            ).show()"""
        new_start_time_picker = "showStartTimePicker = true"
        content = content.replace(old_start_time_picker, new_start_time_picker)

        # For alarm1Time
        old_alarm1_time_picker = """                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val newCal = Calendar.getInstance().apply {
                                        timeInMillis = dateMillis
                                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        set(Calendar.MINUTE, minute)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    alarm1Time = newCal.timeInMillis
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                false
                            ).show()"""
        new_alarm1_time_picker = "showAlarm1Picker = true"
        content = content.replace(old_alarm1_time_picker, new_alarm1_time_picker)

        # For alarm2Time
        old_alarm2_time_picker = """                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val newCal = Calendar.getInstance().apply {
                                        timeInMillis = dateMillis
                                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        set(Calendar.MINUTE, minute)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    alarm2Time = newCal.timeInMillis
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                false
                            ).show()"""
        new_alarm2_time_picker = "showAlarm2Picker = true"
        content = content.replace(old_alarm2_time_picker, new_alarm2_time_picker)

        # Append pickers_code right after AlertDialog(
        # The easiest way is to wrap AlertDialog in a Column or Box, but wait, StudyPlanEntryDialog is just a @Composable function, it can just have multiple top-level siblings if we wrap them.
        # But if it's called conditionally `if (showPlanDialog) { StudyPlanEntryDialog(...) }`, it's fine if it emits multiple children.
        
        # Let's replace `AlertDialog(` with `AlertDialog(...) \n pickers_code` by putting pickers_code at the very end of the function.
        # The last line of the function is likely `}`
        # Let's just find the last `}`.
        content = content.rsplit('}', 1)[0] + pickers_code + '\n}'

    with open(filepath, 'w') as f:
        f.write(content)

patch_file('app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt')
try:
    patch_file('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt')
except Exception as e:
    print(e)
