import re

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

imports = """import com.example.utils.LocaleManager
import androidx.compose.ui.res.stringResource
import com.example.R
"""
content = content.replace("import com.example.utils.SettingsPreferences", "import com.example.utils.SettingsPreferences\n" + imports)

lang_card = """            var expandedLanguage by remember { mutableStateOf(false) }
            val currentLang by LocaleManager.currentLanguage.collectAsState()
            
            val languages = listOf(
                "en" to "English",
                "hi" to "हिन्दी (Hindi)",
                "fr" to "Français",
                "it" to "Italiano",
                "es" to "Español",
                "pt" to "Português"
            )

            Card {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(stringResource(R.string.language), style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedLanguage,
                        onExpandedChange = { expandedLanguage = !expandedLanguage }
                    ) {
                        OutlinedTextField(
                            value = languages.find { it.first == currentLang }?.second ?: "English",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLanguage) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedLanguage,
                            onDismissRequest = { expandedLanguage = false }
                        ) {
                            languages.forEach { (code, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        LocaleManager.setLocale(context, code)
                                        expandedLanguage = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
"""

content = content.replace("            Card {\n                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {\n                    Text(\"Snooze", lang_card + "\n            Card {\n                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {\n                    Text(stringResource(R.string.snooze_duration)")

content = content.replace('Text("Settings")', 'Text(stringResource(R.string.settings))')
content = content.replace('Text("Premium Active"', 'Text(stringResource(R.string.premium_active)"')
content = content.replace('Text("You have access to all features."', 'Text(stringResource(R.string.access_all_features)"')
content = content.replace('Text("Upgrade to Premium"', 'Text(stringResource(R.string.upgrade_to_premium)"')
content = content.replace('Text("Snooze Duration"', 'Text(stringResource(R.string.snooze_duration)"')
content = content.replace('Text("About"', 'Text(stringResource(R.string.about)"')
content = content.replace('Text("Study Planner Version 1.0"', 'Text(stringResource(R.string.study_planner_version)"')
content = content.replace('Text("Focus better and achieve your study goals."', 'Text(stringResource(R.string.focus_better)"')

# Oops, the replacements might be slightly off. I'll just write a safer python script to replace the literals.

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)
