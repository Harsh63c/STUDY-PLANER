with open("app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt", "r") as f:
    content = f.read()

# Fix alarms
content = content.replace(
    'if (alarm1Time != null) cal.timeInMillis = alarm1Time!!showAlarm1Picker = true',
    'if (alarm1Time != null) cal.timeInMillis = alarm1Time!!\n                            showAlarm1Picker = true'
)
content = content.replace(
    'if (alarm2Time != null) cal.timeInMillis = alarm2Time!!showAlarm2Picker = true',
    'if (alarm2Time != null) cal.timeInMillis = alarm2Time!!\n                            showAlarm2Picker = true'
)

# Fix strings
content = content.replace('stringResource(R.string.previous_month)', '"Previous Month"')
content = content.replace('stringResource(R.string.next_month)', '"Next Month"')

# Fix stringResources outside Composable
# Line 198: val strDaily = stringResource(R.string.daily) etc..
# It is better to just revert everything for previous/next month and repeat mode.

with open("app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt", "w") as f:
    f.write(content)
