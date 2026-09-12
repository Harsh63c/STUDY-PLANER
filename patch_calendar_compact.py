import re

with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'r') as f:
    content = f.read()

# 1. MonthlyCalendar padding
content = content.replace('Column(modifier = Modifier.fillMaxWidth().padding(16.dp))', 'Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp))')

# 2. Month title row and style
content = content.replace(
'''        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(-1) }) {
                Text("<")
            }
            Text(text = monthTitle, style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { onMonthChanged(1) }) {
                Text(">")
            }
        }''',
'''        Row(
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
        }'''
)

# 3. Spacers
content = content.replace('Spacer(modifier = Modifier.height(16.dp))', 'Spacer(modifier = Modifier.height(4.dp))')
content = content.replace('Spacer(modifier = Modifier.height(8.dp))', 'Spacer(modifier = Modifier.height(2.dp))')

# 4. Aspect ratio in Box
content = content.replace(
'''                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clickable { onDateSelected(cellDateMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)''',
'''                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .padding(1.dp)
                                .clickable { onDateSelected(cellDateMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)'''
)

# 5. Aspect ratio in Spacer
content = content.replace('Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))', 'Spacer(modifier = Modifier.weight(1f).height(36.dp))')


# Write changes
with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'w') as f:
    f.write(content)
