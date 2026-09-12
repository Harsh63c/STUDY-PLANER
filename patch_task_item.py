import re

with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'r') as f:
    content = f.read()

new_item = """fun TaskItem(task: Task, onToggleCompletion: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                details += " • Repeat: Once"
                
                Text(
                    text = details,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Task", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}"""

start_idx = content.find("fun TaskItem(")
# Find the matching closing brace for TaskItem
brace_count = 0
found_brace = False
end_idx = -1
for i in range(start_idx, len(content)):
    if content[i] == '{':
        brace_count += 1
        found_brace = True
    elif content[i] == '}':
        brace_count -= 1
    if found_brace and brace_count == 0:
        end_idx = i + 1
        break

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + new_item + content[end_idx:]
    with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'w') as f:
        f.write(content)
    print("TaskItem replaced")
else:
    print("Could not find TaskItem")
