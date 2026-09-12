import re

# CalendarScreen.kt
with open("app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "fun TaskItem(task: Task, onToggleCompletion: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {",
    "fun TaskItem(task: Task, onToggleCompletion: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit, onSkipNext: () -> Unit = {}) {"
)
content = content.replace(
    "IconButton(onClick = { /* TODO: Skip logic could be handled if we passed an onSkip */ }",
    "IconButton(onClick = onSkipNext"
)
# We also need to add a visual indicator if it is skipped
skip_indicator = """                if (task.repeatMode != "None") {
                    if (task.skipNextOccurrence) {
                        Text(text = " (Next Skipped)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    }
                }
"""
content = content.replace('details += " • Repeat: ${task.repeatMode}"\n                }\n', 'details += " • Repeat: ${task.repeatMode}"\n                }\n' + skip_indicator)

# Hook it up in LazyColumn
lazy_col_item = """TaskItem(
                            task = task,
                            onToggleCompletion = {
                                viewModel.updateTask(task.copy(isCompleted = !task.isCompleted))
                            },
                            onEdit = {
                                selectedTask = task
                                showAddDialog = true
                            },
                            onDelete = {
                                viewModel.deleteTask(task)
                            },
                            onSkipNext = {
                                viewModel.updateTask(task.copy(skipNextOccurrence = !task.skipNextOccurrence))
                            }
                        )"""
content = re.sub(r'TaskItem\(\s*task = task,\s*onToggleCompletion = \{[\s\S]*?viewModel\.updateTask\(task\.copy\(isCompleted = !task\.isCompleted\)\)\s*\},[\s\S]*?onEdit = \{[\s\S]*?selectedTask = task\s*showAddDialog = true\s*\},[\s\S]*?onDelete = \{[\s\S]*?viewModel\.deleteTask\(task\)\s*\}\s*\)', lazy_col_item, content)

with open("app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt", "w") as f:
    f.write(content)

# StudyPlannerScreen.kt
with open("app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "fun StudyPlanItem(plan: StudyPlan, onEdit: () -> Unit, onDelete: () -> Unit) {",
    "fun StudyPlanItem(plan: StudyPlan, onEdit: () -> Unit, onDelete: () -> Unit, onSkipNext: () -> Unit = {}) {"
)
content = content.replace(
    "IconButton(onClick = { /* TODO: Skip logic */ }",
    "IconButton(onClick = onSkipNext"
)

# visual indicator
skip_indicator_plan = """                if (plan.repeatMode != "None") {
                    if (plan.skipNextOccurrence) {
                        Text(text = " (Next Skipped)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                    }
                }
"""
content = content.replace('details += " • Repeat: ${plan.repeatMode}"\n                }\n', 'details += " • Repeat: ${plan.repeatMode}"\n                }\n' + skip_indicator_plan)

# Hook it up in LazyColumn
lazy_col_plan = """StudyPlanItem(
                        plan = plan,
                        onEdit = {
                            selectedPlan = plan
                            showDialog = true
                        },
                        onDelete = { viewModel.deleteStudyPlan(plan) },
                        onSkipNext = {
                            viewModel.updateStudyPlan(plan.copy(skipNextOccurrence = !plan.skipNextOccurrence))
                        }
                    )"""
content = re.sub(r'StudyPlanItem\(\s*plan = plan,\s*onEdit = \{[\s\S]*?selectedPlan = plan\s*showDialog = true\s*\},\s*onDelete = \{ viewModel\.deleteStudyPlan\(plan\) \}\s*\)', lazy_col_plan, content)

with open("app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt", "w") as f:
    f.write(content)

