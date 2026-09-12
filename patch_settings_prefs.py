import re

with open("app/src/main/java/com/example/utils/SettingsPreferences.kt", "r") as f:
    content = f.read()

prefs = """    private const val KEY_LANGUAGE = "app_language"

    fun getLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguage(context: Context, languageCode: String) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, languageCode).apply()
    }
"""

content = content.replace("    fun getSnoozeDuration(context: Context): Int {", prefs + "\n    fun getSnoozeDuration(context: Context): Int {")

with open("app/src/main/java/com/example/utils/SettingsPreferences.kt", "w") as f:
    f.write(content)
