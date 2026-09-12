import re

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

content = content.replace('Text(stringResource(R.string.premium_active)", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)', 'Text(stringResource(R.string.premium_active), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)')
content = content.replace('Text(stringResource(R.string.access_all_features)", style = MaterialTheme.typography.bodyMedium)', 'Text(stringResource(R.string.access_all_features), style = MaterialTheme.typography.bodyMedium)')
content = content.replace('Text(stringResource(R.string.upgrade_to_premium)", style = MaterialTheme.typography.titleLarge)', 'Text(stringResource(R.string.upgrade_to_premium), style = MaterialTheme.typography.titleLarge)')
content = content.replace('Text(stringResource(R.string.about)", style = MaterialTheme.typography.titleLarge)', 'Text(stringResource(R.string.about), style = MaterialTheme.typography.titleLarge)')
content = content.replace('Text(stringResource(R.string.study_planner_version)", style = MaterialTheme.typography.bodyMedium)', 'Text(stringResource(R.string.study_planner_version), style = MaterialTheme.typography.bodyMedium)')
content = content.replace('Text(stringResource(R.string.focus_better)", style = MaterialTheme.typography.bodySmall)', 'Text(stringResource(R.string.focus_better), style = MaterialTheme.typography.bodySmall)')

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)
