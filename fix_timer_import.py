with open("app/src/main/java/com/example/ui/screens/studyplanner/TimerScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.Alignment", "import androidx.compose.ui.Alignment\nimport androidx.compose.ui.res.stringResource\nimport com.example.R")

with open("app/src/main/java/com/example/ui/screens/studyplanner/TimerScreen.kt", "w") as f:
    f.write(content)
