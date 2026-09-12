import re

with open("app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt", "r") as f:
    content = f.read()

imports = """import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.utils.LocaleManager
"""
content = content.replace("import androidx.compose.material3.*", "import androidx.compose.material3.*\n" + imports)

set_content = """        LocaleManager.init(this)
        setContent {
            val languageCode by LocaleManager.currentLanguage.collectAsState()
            val localizedContext = LocaleManager.getLocalizedContext(this, languageCode)
            val configuration = localizedContext.resources.configuration

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides configuration
            ) {
                MyApplicationTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AlarmScreenContent(
                            title = title,
                            message = message,
                            timeString = currentTime,
                            snoozeDuration = snoozeDuration,
                            onDismiss = {
                                dismissAlarm()
                                finish()
                            },
                            onSnooze = {
                                snoozeAlarm(title, message, notifId, isStudyPlan, snoozeDuration)
                                finish()
                            }
                        )
                    }
                }
            }
        }"""
content = re.sub(r'setContent \{\s*MyApplicationTheme \{[\s\S]*?finish\(\)\s*\}\s*\)\s*\}\s*\}\s*\}', set_content, content)

with open("app/src/main/java/com/example/ui/screens/studyplanner/AlarmRingingActivity.kt", "w") as f:
    f.write(content)

