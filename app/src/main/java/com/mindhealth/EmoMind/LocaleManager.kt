package com.mindhealth.EmoMind

import android.content.Context
import android.content.res.Configuration
import android.content.SharedPreferences
import java.util.Locale

object LocaleManager {

    private const val PREFS_NAME = "app_prefs"
    private const val LANGUAGE_KEY = "language"

    fun setLocale(context: Context, lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        updateResources(context, locale)
        saveLanguage(context, lang)
    }

    fun updateResources(context: Context, locale: Locale) {
        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getCurrentLocale(context: Context): String {
        return getSavedLanguage(context)
    }

    private fun saveLanguage(context: Context, lang: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(LANGUAGE_KEY, lang).apply()
    }

    private fun getSavedLanguage(context: Context): String {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(LANGUAGE_KEY, "ru") ?: "ru"
    }

    fun changeLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }
}