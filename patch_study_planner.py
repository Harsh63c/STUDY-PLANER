with open("app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.text.font.FontWeight", "import androidx.compose.ui.text.font.FontWeight\nimport androidx.compose.ui.res.stringResource\nimport com.example.R")

replace_map = {
    '"Add Study Plan"': 'stringResource(R.string.add_study_plan)',
    '"Edit Study Plan"': 'stringResource(R.string.edit_study_plan)',
    '"Subject"': 'stringResource(R.string.subject)',
    '"Chapter / Topic"': 'stringResource(R.string.chapter_topic)',
    '"Target Duration (mins)"': 'stringResource(R.string.target_duration)',
    '"Delete Plan"': 'stringResource(R.string.delete_plan)',
    '"Edit Plan"': 'stringResource(R.string.edit_plan)',
    '"Start Study"': 'stringResource(R.string.start_study)',
    '"No study plans scheduled for this month."': 'stringResource(R.string.no_plans_month)',
    '"In Progress"': 'stringResource(R.string.in_progress)',
    '"Cancel"': 'stringResource(R.string.cancel)',
    '"Save"': 'stringResource(R.string.save)',
    '"Change"': 'stringResource(R.string.change)',
    '"Study Planner"': 'stringResource(R.string.study_planner)',
    '"Focus Lock"': 'stringResource(R.string.focus_lock)',
    '"Date: ${dateFormat.format(dateMillis)}"': 'stringResource(R.string.date, dateFormat.format(dateMillis))',
    '"Alarm 1: ${alarm1Time?.let { timeFormat.format(it) } ?: "': 'stringResource(R.string.alarm_1, alarm1Time?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)) + "',
    '"Alarm 2: ${alarm2Time?.let { timeFormat.format(it) } ?: "': 'stringResource(R.string.alarm_2, alarm2Time?.let { timeFormat.format(it) } ?: stringResource(R.string.not_set)) + "',
    '" • Repeat: ${plan.repeatMode}"': '(" • " + stringResource(R.string.repeat_mode, plan.repeatMode))',
    '" • Repeat: Once"': '(" • " + stringResource(R.string.repeat_once))',
    '"$dateString • $timeString • Target: ${plan.targetDurationMinutes}m"': '("$dateString • $timeString • " + stringResource(R.string.target_study, plan.targetDurationMinutes.toString()))',
    '" (Next Skipped)"': '(" " + stringResource(R.string.next_skipped))',
    '"Skip Next Occurrence"': 'stringResource(R.string.skip_next)',
    '"Repeat Schedule"': 'stringResource(R.string.repeat_schedule)',
    '"Select Days:"': 'stringResource(R.string.select_days)',
    '"None"': 'stringResource(R.string.none)',
    '"Daily"': 'stringResource(R.string.daily)',
    '"Weekdays"': 'stringResource(R.string.weekdays)',
    '"Weekends"': 'stringResource(R.string.weekends)',
    '"Custom"': 'stringResource(R.string.custom)',
    '"S"': 'stringResource(R.string.day_s)',
    '"M"': 'stringResource(R.string.day_m)',
    '"T"': 'stringResource(R.string.day_t)',
    '"W"': 'stringResource(R.string.day_w)',
    '"F"': 'stringResource(R.string.day_f)',
    '"Next Month"': 'stringResource(R.string.next_month)',
    '"Previous Month"': 'stringResource(R.string.previous_month)',
}

for k, v in replace_map.items():
    content = content.replace(k, v)

with open("app/src/main/java/com/example/ui/screens/studyplanner/StudyPlannerScreen.kt", "w") as f:
    f.write(content)
