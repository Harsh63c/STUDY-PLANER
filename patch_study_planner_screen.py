import re

with open("app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt", "r") as f:
    content = f.read()

# Replace "Repeat: Once" in StudyPlanItem
new_repeat_text = """
                if (plan.repeatMode != "None") {
                    details += " • Repeat: ${plan.repeatMode}"
                }
"""
content = content.replace('details += " • Repeat: Once"', new_repeat_text)

# Add skip button to StudyPlanItem
skip_button = """                if (plan.repeatMode != "None") {
                    IconButton(onClick = { /* TODO: Skip logic */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Skip Next Occurrence", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
                if (plan.status == "Scheduled" || plan.status.contains("Alarm") || plan.status == "Waiting for Backup Alarm") {"""
content = content.replace("import androidx.compose.material.icons.filled.CheckCircle", "import androidx.compose.material.icons.filled.CheckCircle\nimport androidx.compose.material.icons.filled.SkipNext")
content = content.replace("if (plan.status == \"Scheduled\" || plan.status.contains(\"Alarm\") || plan.status == \"Waiting for Backup Alarm\") {", skip_button)

# Dialog state
dialog_state_add = """    var showAlarm2Picker by remember { mutableStateOf(false) }
    var repeatMode by remember { mutableStateOf(initialPlan?.repeatMode ?: "None") }
    var customRepeatDays by remember { mutableStateOf(initialPlan?.customRepeatDays ?: "") }
    var skipNextOccurrence by remember { mutableStateOf(initialPlan?.skipNextOccurrence ?: false) }
"""
content = re.sub(r'var showAlarm2Picker by remember \{ mutableStateOf\(false\) \}', dialog_state_add, content)

# Dropdown for repeat mode
repeat_ui = """
                item {
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
                }
                
                if (repeatMode == "Custom") {
                    item {
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
                }
"""

content = content.replace("                item {\n                    OutlinedTextField(\n                        value = notes,", repeat_ui + "\n                item {\n                    OutlinedTextField(\n                        value = notes,")

save_plan = """
                onSave(
                    StudyPlan(
                        id = initialPlan?.id ?: 0,
                        subject = subject,
                        chapter = chapter,
                        dateMillis = dateMillis,
                        startTimeMillis = startTime,
                        targetDurationMinutes = targetDuration.toIntOrNull() ?: 60,
                        notes = notes,
                        alarm1TimeMillis = alarm1Time,
                        alarm2TimeMillis = alarm2Time,
                        isCompleted = initialPlan?.isCompleted ?: false,
                        inProgress = initialPlan?.inProgress ?: false,
                        status = initialPlan?.status ?: "Scheduled",
                        repeatMode = repeatMode,
                        customRepeatDays = customRepeatDays,
                        skipNextOccurrence = skipNextOccurrence
                    )
                )"""
content = re.sub(r'onSave\(\s*StudyPlan\([\s\S]*?status = initialPlan\?\.status \?: "Scheduled"\s*\)\s*\)', save_plan, content)

with open("app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt", "w") as f:
    f.write(content)
