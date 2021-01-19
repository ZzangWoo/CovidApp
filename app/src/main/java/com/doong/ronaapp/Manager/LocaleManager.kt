package com.doong.ronaapp.Manager

import android.content.Context
import android.content.res.Configuration
import com.doong.ronaapp.App
import java.util.*

object LocaleManager {

    fun setLocale(context: Context): Context {
        return if (App.prefs.locale != null)
                updateResources(context, App.prefs.locale!!)
            else
                context
    }

    fun setNewLocale(context: Context, language: String): Context {
        App.prefs.locale = language
        return updateResources(context, language)
    }

    private fun updateResources(context: Context, language: String): Context {
        var localContext = context
        var locale = Locale(language)
        Locale.setDefault(locale)

        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocale(locale)
        localContext = context.createConfigurationContext(config)
        return localContext
    }
}