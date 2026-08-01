package com.example.calculatoremi.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREF_NAME = "theme_prefs"
    const val KEY_THEME_MODE = "key_theme_mode"

    const val THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
    const val THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES
    const val THEME_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Initializes theme on app startup.
     */
    fun initTheme(context: Context) {
        val savedMode = getSavedThemeMode(context)
        AppCompatDelegate.setDefaultNightMode(savedMode)
    }

    /**
     * Returns the currently saved theme mode.
     */
    fun getSavedThemeMode(context: Context): Int {
        return getPreferences(context).getInt(KEY_THEME_MODE, THEME_SYSTEM)
    }

    /**
     * Applies and saves the chosen theme mode.
     */
    fun applyTheme(context: Context, mode: Int) {
        getPreferences(context).edit().putInt(KEY_THEME_MODE, mode).apply()
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    /**
     * Returns true if dark mode is currently active.
     */
    fun isDarkMode(context: Context): Boolean {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        return if (currentMode == THEME_SYSTEM) {
            val nightModeFlags = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
            nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
        } else {
            currentMode == THEME_DARK
        }
    }
}
