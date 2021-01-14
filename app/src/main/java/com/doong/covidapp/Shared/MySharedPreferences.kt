package com.doong.covidapp.Shared

import android.content.Context
import android.content.SharedPreferences

class MySharedPreferences(context: Context) {

    private val prefsFilename = "prefs"
    private val prefsLocaleKey = "locale"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, 0)

    var locale: String?
        get() = prefs.getString(prefsLocaleKey, "en")
        set(value) = prefs.edit().putString(prefsLocaleKey, value).apply()

}