with open("app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.res.painterResource", "import androidx.compose.ui.res.painterResource\nimport androidx.compose.ui.res.stringResource\nimport com.example.R")

with open("app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt", "w") as f:
    f.write(content)
