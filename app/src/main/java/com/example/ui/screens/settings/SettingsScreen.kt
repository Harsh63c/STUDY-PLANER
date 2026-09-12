package com.example.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.services.BillingManager
import com.example.utils.SettingsPreferences
import com.example.utils.LocaleManager
import androidx.compose.ui.res.stringResource
import com.example.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val billingManager = remember { BillingManager(context) }
    
    LaunchedEffect(Unit) {
        billingManager.startConnection()
    }

    val isPremium by billingManager.isPremium.collectAsState(initial = false)

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.settings)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isPremium) {
                Card {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text(stringResource(R.string.premium_active), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Text(stringResource(R.string.access_all_features), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                Card {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text(stringResource(R.string.upgrade_to_premium), style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        
                        
                        
                    }
                }
            }
            

            var snoozeDuration by remember { mutableStateOf(SettingsPreferences.getSnoozeDuration(context)) }
            
            var expandedLanguage by remember { mutableStateOf(false) }
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

            Card {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(stringResource(R.string.snooze_duration), style = MaterialTheme.typography.titleLarge)
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

            Card {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text(stringResource(R.string.about), style = MaterialTheme.typography.titleLarge)
                    Text(stringResource(R.string.study_planner_version), style = MaterialTheme.typography.bodyMedium)
                    Text(stringResource(R.string.focus_better), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
