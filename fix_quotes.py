files = ["app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt", 
         "app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt"]

for file in files:
    with open(file, "r") as f:
        content = f.read()

    content = content.replace(
        'Text("Alarm 1: ${alarm1Time?.let { timeFormat.format(it) } ?: \\"Not set\\"}")',
        'Text("Alarm 1: " + (alarm1Time?.let { timeFormat.format(it) } ?: "Not set"))'
    )
    content = content.replace(
        'Text("Alarm 2: ${alarm2Time?.let { timeFormat.format(it) } ?: \\"Not set\\"}")',
        'Text("Alarm 2: " + (alarm2Time?.let { timeFormat.format(it) } ?: "Not set"))'
    )
    content = content.replace(
        'Text("Start Time: ${startTime?.let { timeFormat.format(it) } ?: \\"Not set\\"}")',
        'Text("Start Time: " + (startTime?.let { timeFormat.format(it) } ?: "Not set"))'
    )
    content = content.replace(
        'Text("End Time: ${endTime?.let { timeFormat.format(it) } ?: \\"Not set\\"}")',
        'Text("End Time: " + (endTime?.let { timeFormat.format(it) } ?: "Not set"))'
    )
    content = content.replace(
        'Text("Reminder: ${reminderTime?.let { timeFormat.format(it) } ?: \\"Not set\\"}")',
        'Text("Reminder: " + (reminderTime?.let { timeFormat.format(it) } ?: "Not set"))'
    )
    
    # Also I'll check if there are \"Not set\" left
    content = content.replace('\\"Not set\\"', '"Not set"')

    with open(file, "w") as f:
        f.write(content)

