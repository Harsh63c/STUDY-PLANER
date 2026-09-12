import re

with open('app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt', 'r') as f:
    content = f.read()

new_item = """fun StudyPlanItem(
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
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start Study", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Plan", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Plan", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}"""

# Find the old function and replace it
pattern = re.compile(r'fun StudyPlanItem\(.*?\}', re.DOTALL)

# We need to be careful with nested braces. Let's just find the start and end precisely.
# A better way is using string manipulation.

start_idx = content.find("fun StudyPlanItem(")
end_idx = content.find("\n}\n", start_idx) + 3

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + new_item + content[end_idx:]
    with open('app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt', 'w') as f:
        f.write(content)
    print("StudyPlanItem replaced")
else:
    print("Could not find StudyPlanItem")
