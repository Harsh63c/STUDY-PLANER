import re

with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
'''            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),''',
'''            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),'''
)

content = content.replace(
'''                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)''',
'''                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)'''
)

with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'w') as f:
    f.write(content)
