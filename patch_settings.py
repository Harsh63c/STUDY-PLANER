import re

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

imports = """import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.services.BillingManager
import com.example.utils.SettingsPreferences
"""
content = re.sub(r'import androidx\.compose\.foundation\.layout\.\*.*import com\.example\.services\.BillingManager', imports, content, flags=re.DOTALL)

snooze_card = """
            var snoozeDuration by remember { mutableStateOf(SettingsPreferences.getSnoozeDuration(context)) }
            
            Card {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("Snooze Duration", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(5, 10, 15).forEach { min ->
                            FilterChip(
                                selected = (snoozeDuration == min),
                                onClick = {
                                    snoozeDuration = min
                                    SettingsPreferences.setSnoozeDuration(context, min)
                                },
                                label = { Text("${min}m") }
                            )
                        }
                    }
                }
            }
"""

content = content.replace("            Card {\n                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {\n                    Text(\"About\"", snooze_card + "\n            Card {\n                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {\n                    Text(\"About\"")

with open("app/src/main/java/com/example/ui/screens/settings/SettingsScreen.kt", "w") as f:
    f.write(content)
