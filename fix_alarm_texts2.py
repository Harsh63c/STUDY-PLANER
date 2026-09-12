with open("app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt", "r") as f:
    content = f.read()

content = content.replace('Text(stringResource(R.string.dismiss)", style = MaterialTheme.typography.headlineSmall)', 'Text(stringResource(R.string.dismiss), style = MaterialTheme.typography.headlineSmall)')
content = content.replace('Text(stringResource(R.string.snooze, snoozeDuration.toString())", style = MaterialTheme.typography.titleMedium)', 'Text(stringResource(R.string.snooze, snoozeDuration.toString()), style = MaterialTheme.typography.titleMedium)')

with open("app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt", "w") as f:
    f.write(content)
