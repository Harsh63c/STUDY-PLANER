import re

with open("app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt", "r") as f:
    content = f.read()

# Replace TaskItem repeat hardcode
# details += " • Repeat: Once"
new_repeat_text = """
                if (task.repeatMode != "None") {
                    details += " • Repeat: ${task.repeatMode}"
                }
"""
content = content.replace('details += " • Repeat: Once"', new_repeat_text)

# Add skip button in TaskItem
skip_button = """                if (task.repeatMode != "None") {
                    IconButton(onClick = { /* TODO: Skip logic could be handled if we passed an onSkip */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Skip Next Occurrence", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
                IconButton(onClick = onEdit"""
# We need Icons.Default.SkipNext
content = content.replace("import androidx.compose.material.icons.filled.CheckCircle", "import androidx.compose.material.icons.filled.CheckCircle\nimport androidx.compose.material.icons.filled.SkipNext")
content = content.replace("IconButton(onClick = onEdit", skip_button)

# Dialog state
dialog_state_add = """    var reminderTime by remember { mutableStateOf(initialTask?.reminderTimeMillis) }
    var repeatMode by remember { mutableStateOf(initialTask?.repeatMode ?: "None") }
    var customRepeatDays by remember { mutableStateOf(initialTask?.customRepeatDays ?: "") }
    var skipNextOccurrence by remember { mutableStateOf(initialTask?.skipNextOccurrence ?: false) }
"""
content = re.sub(r'var reminderTime by remember \{ mutableStateOf\(initialTask\?\.reminderTimeMillis\) \}', dialog_state_add, content)

# Dropdown for repeat mode
repeat_ui = """
                // Repeat Option
                var repeatExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = repeatExpanded,
                    onExpandedChange = { repeatExpanded = !repeatExpanded }
                ) {
                    OutlinedTextField(
                        value = repeatMode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Repeat Schedule") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = repeatExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = repeatExpanded,
                        onDismissRequest = { repeatExpanded = false }
                    ) {
                        listOf("None", "Daily", "Weekdays", "Weekends", "Custom").forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    repeatMode = selectionOption
                                    repeatExpanded = false
                                }
                            )
                        }
                    }
                }
                
                if (repeatMode == "Custom") {
                    Text("Select Days:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val days = listOf("S" to "1", "M" to "2", "T" to "3", "W" to "4", "T" to "5", "F" to "6", "S" to "7")
                        days.forEach { (label, value) ->
                            val isSelected = customRepeatDays.split(",").contains(value)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val currentList = customRepeatDays.split(",").filter { it.isNotEmpty() }.toMutableList()
                                    if (isSelected) currentList.remove(value) else currentList.add(value)
                                    customRepeatDays = currentList.joinToString(",")
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }
"""

content = content.replace("                Row(\n                    verticalAlignment = Alignment.CenterVertically,\n                    horizontalArrangement = Arrangement.SpaceBetween,\n                    modifier = Modifier.fillMaxWidth()\n                ) {\n                    Text(\"Reminder Time:", repeat_ui + "\n                Row(\n                    verticalAlignment = Alignment.CenterVertically,\n                    horizontalArrangement = Arrangement.SpaceBetween,\n                    modifier = Modifier.fillMaxWidth()\n                ) {\n                    Text(\"Reminder Time:")

save_task = """
                onSave(
                    Task(
                        id = initialTask?.id ?: 0,
                        title = title,
                        dateMillis = dateMillis,
                        startTimeMillis = startTime,
                        endTimeMillis = endTime,
                        reminderTimeMillis = reminderTime,
                        isCompleted = initialTask?.isCompleted ?: false,
                        repeatMode = repeatMode,
                        customRepeatDays = customRepeatDays,
                        skipNextOccurrence = skipNextOccurrence
                    )
                )"""
content = re.sub(r'onSave\(\s*Task\([\s\S]*?isCompleted = initialTask\?\.isCompleted \?: false\s*\)\s*\)', save_task, content)

with open("app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt", "w") as f:
    f.write(content)

