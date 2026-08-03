package com.example.calculatoremi.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import com.example.calculatoremi.model.LanguageItem
import java.util.Locale

object LanguageManager {

    private const val PREFS_NAME = "language_preferences"
    private const val KEY_LANGUAGE_CODE = "selected_language_code"

    val DEFAULT_LANGUAGE = LanguageItem("en", "English", "English", "🇺🇸")

    val ALL_LANGUAGES = listOf(
        DEFAULT_LANGUAGE,
        LanguageItem("de", "German", "Deutsch", "🇩🇪"),
        LanguageItem("fr", "French", "Français", "🇫🇷"),
        LanguageItem("es", "Spanish", "Español", "🇪🇸"),
        LanguageItem("nl", "Dutch", "Nederlands", "🇳🇱"),
        LanguageItem("it", "Italian", "Italiano", "🇮🇹"),
        LanguageItem("pt", "Portuguese", "Português", "🇵🇹"),
        LanguageItem("ja", "Japanese", "日本語", "🇯🇵"),
        LanguageItem("ko", "Korean", "한국어", "🇰🇷"),
        LanguageItem("pl", "Polish", "Polski", "🇵🇱"),
        LanguageItem("ar", "Arabic", "العربية", "🇸🇦"),
        LanguageItem("sv", "Swedish", "Svenska", "🇸🇪"),
        LanguageItem("nb", "Norwegian", "Norsk", "🇳🇴"),
        LanguageItem("da", "Danish", "Dansk", "🇩🇰"),
        LanguageItem("fi", "Finnish", "Suomi", "🇫🇮"),
        LanguageItem("tr", "Turkish", "Türkçe", "🇹🇷"),
        LanguageItem("id", "Indonesian", "Bahasa Indonesia", "🇮🇩"),
        LanguageItem("ms", "Malay", "Bahasa Melayu", "🇲🇾"),
        LanguageItem("fil", "Filipino", "Tagalog / Filipino", "🇵🇭"),
        LanguageItem("vi", "Vietnamese", "Tiếng Việt", "🇻🇳"),
        LanguageItem("th", "Thai", "ไทย", "🇹🇭"),
        LanguageItem("hi", "Hindi", "हिन्दी", "🇮🇳"),
        LanguageItem("ta", "Tamil", "தமிழ்", "🇮🇳"),
        LanguageItem("te", "Telugu", "తెలుగు", "🇮🇳"),
        LanguageItem("mr", "Marathi", "मराठी", "🇮🇳")
    )

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSelectedLanguage(context: Context): LanguageItem {
        val prefs = getPrefs(context)
        val code = prefs.getString(KEY_LANGUAGE_CODE, DEFAULT_LANGUAGE.languageCode) ?: DEFAULT_LANGUAGE.languageCode
        return ALL_LANGUAGES.find { it.languageCode.equals(code, ignoreCase = true) } ?: DEFAULT_LANGUAGE
    }

    fun setLanguage(context: Context, language: LanguageItem) {
        getPrefs(context)
            .edit()
            .putString(KEY_LANGUAGE_CODE, language.languageCode)
            .apply()
        applyLocale(context, language.languageCode)
    }

    fun applyLocale(context: Context, languageCode: String = getSelectedLanguage(context).languageCode): Context {
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        return context.createConfigurationContext(config)
    }

    fun wrapContext(context: Context): ContextWrapper {
        val languageCode = getSelectedLanguage(context).languageCode
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        return ContextWrapper(context.createConfigurationContext(config))
    }
}
