import re

with open("app/src/main/java/com/example/ui/screens/studyplanner/FocusLockSetupScreen.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.compose.ui.text.font.FontWeight", "import androidx.compose.ui.text.font.FontWeight\nimport androidx.compose.ui.res.stringResource\nimport com.example.R")

replace_map = {
    '"Focus Lock Setup"': 'stringResource(R.string.focus_lock_setup)',
    '"Back"': 'stringResource(R.string.back)',
    '"Cancel Focus Session"': 'stringResource(R.string.cancel_focus_session)',
    '"Focus Lock helps you avoid distracting apps while studying."': 'stringResource(R.string.focus_lock_help)',
    '"Grant Usage Access Permission"': 'stringResource(R.string.grant_usage_access)',
    '"I Agree, Start"': 'stringResource(R.string.i_agree_start)',
    '"Please choose the apps carefully. Once Focus Lock starts, the selected apps will remain blocked until the selected session ends. If you select an app by mistake, you may not be able to use it until the session finishes."': 'stringResource(R.string.focus_lock_warning)',
    '"Start Focus Lock (Min 1 Hour)"': 'stringResource(R.string.start_focus_lock_min)',
    '"Start Focus Lock?"': 'stringResource(R.string.start_focus_lock_prompt)',
    '"Cancel"': 'stringResource(R.string.cancel)',
}

for k, v in replace_map.items():
    content = content.replace(k, v)

with open("app/src/main/java/com/example/ui/screens/studyplanner/FocusLockSetupScreen.kt", "w") as f:
    f.write(content)

