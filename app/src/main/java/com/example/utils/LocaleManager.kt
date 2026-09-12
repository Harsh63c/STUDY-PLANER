package com.example.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LocaleManager {
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage

    fun init(context: Context) {
        _currentLanguage.value = SettingsPreferences.getLanguage(context)
    }

    fun setLocale(context: Context, languageCode: String) {
        SettingsPreferences.setLanguage(context, languageCode)
        _currentLanguage.value = languageCode
    }

    fun getLocalizedContext(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
