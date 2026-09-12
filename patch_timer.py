with open("app/src/main/java/com/example/ui/screens/studyplanner/TimerScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.text.font.FontWeight", "import androidx.compose.ui.text.font.FontWeight\nimport androidx.compose.ui.res.stringResource\nimport com.example.R")

content = content.replace('Text("Study Timer")', 'Text(stringResource(R.string.study_timer))')
content = content.replace('Text("Pause")', 'Text(stringResource(R.string.pause))')
content = content.replace('Text("Start")', 'Text(stringResource(R.string.start))')
content = content.replace('Text("Stop & Save")', 'Text(stringResource(R.string.stop_and_save))')

with open("app/src/main/java/com/example/ui/screens/studyplanner/TimerScreen.kt", "w") as f:
    f.write(content)
