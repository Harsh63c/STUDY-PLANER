with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'r') as f:
    content = f.read()

# Replace any sequence of } at the end of the file with a single }
import re
content = re.sub(r'\}\s*\}\s*\}\s*$', '}\n', content)

with open('app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt', 'w') as f:
    f.write(content)
