with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

content = content.replace('Text(stringResource(R.string.snooze_duration) Duration", style = MaterialTheme.typography.titleLarge)', 'Text(stringResource(R.string.snooze_duration), style = MaterialTheme.typography.titleLarge)')

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)
