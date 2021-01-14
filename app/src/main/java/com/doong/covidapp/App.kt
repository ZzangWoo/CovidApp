package com.doong.covidapp

import android.app.Application
import com.doong.covidapp.Manager.LocaleManager
import com.doong.covidapp.Shared.MySharedPreferences

class App : Application() {

    companion object {
        lateinit var prefs: MySharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        prefs = MySharedPreferences(applicationContext)


        if (!prefs.locale.isNullOrEmpty()) {
//            val res = resources
//            val dm = res.displayMetrics
//            val conf = res.configuration
//
//
//            if (App.prefs.locale == "kr") {
//                conf.locale = Locale.KOREA
//            } else if (App.prefs.locale == "ja") {
//                conf.locale = Locale.JAPAN
//            } else {
//                conf.locale = Locale.US
//            }
//            resources.updateConfiguration(conf, dm)

            LocaleManager.setNewLocale(this, App.prefs.locale!!)


//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////                conf.setLocale(Locale(App.prefs.locale))
//                applicationContext.resources.configuration.setLocale(Locale(App.prefs.locale))
////                LocaleEntity.locale = Locale(App.prefs.locale)
//                LocaleEntity.locale = applicationContext.resources.configuration.locales.get(0)
//                App.prefs.locale = applicationContext.resources.configuration.locales.get(0).language
//            } else {
////                conf.locale = Locale(App.prefs.locale)
//                applicationContext.resources.configuration.locale = Locale(App.prefs.locale)
//                LocaleEntity.locale = applicationContext.resources.configuration.locale
//                App.prefs.locale = applicationContext.resources.configuration.locale.language
////                LocaleEntity.locale = Locale(App.prefs.locale)
//            }
//
////            res.updateConfiguration(conf, dm)
//            applicationContext.resources.updateConfiguration(conf, dm)
        }
    }

}