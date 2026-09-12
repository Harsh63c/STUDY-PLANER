import re

files = ["app/src/main/java/com/example/ui/screens/calendar/CalendarScreen.kt", 
         "app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt"]

for file in files:
    with open(file, "r") as f:
        content = f.read()

    # Revert all stringResources that might be inside remember blocks or data layer
    content = content.replace('stringResource(R.string.none)', '"None"')
    content = content.replace('stringResource(R.string.daily)', '"Daily"')
    content = content.replace('stringResource(R.string.weekdays)', '"Weekdays"')
    content = content.replace('stringResource(R.string.weekends)', '"Weekends"')
    content = content.replace('stringResource(R.string.custom)', '"Custom"')
    
    # Revert interpolation messes in StudyPlannerScreen
    content = content.replace('stringResource(R.string.alarm_1, alarm1Time?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)) + "', 'Alarm 1: ${alarm1Time?.let { timeFormat.format(it) } ?: "Not set"}"')
    content = content.replace('stringResource(R.string.alarm_2, alarm2Time?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)) + "', 'Alarm 2: ${alarm2Time?.let { timeFormat.format(it) } ?: "Not set"}"')
    
    content = content.replace('(" • " + stringResource(R.string.repeat_mode, plan.repeatMode))', '" • Repeat: ${plan.repeatMode}"')
    content = content.replace('(" • " + stringResource(R.string.repeat_once))', '" • Repeat: Once"')
    content = content.replace('("$dateString • $timeString • " + stringResource(R.string.target_study, plan.targetDurationMinutes.toString()))', '"$dateString • $timeString • Target: ${plan.targetDurationMinutes}m"')
    
    with open(file, "w") as f:
        f.write(content)
