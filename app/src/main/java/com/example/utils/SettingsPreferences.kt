package com.example.utils

import android.content.Context
import android.content.SharedPreferences

object SettingsPreferences {
    private const val PREFS_NAME = "study_planner_prefs"
    private const val KEY_SNOOZE_DURATION = "snooze_duration"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private const val KEY_LANGUAGE = "app_language"

    fun getLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguage(context: Context, languageCode: String) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getSnoozeDuration(context: Context): Int {
        return getPrefs(context).getInt(KEY_SNOOZE_DURATION, 5) // default 5 minutes
    }

    fun setSnoozeDuration(context: Context, durationMinutes: Int) {
        getPrefs(context).edit().putInt(KEY_SNOOZE_DURATION, durationMinutes).apply()
    }
}
