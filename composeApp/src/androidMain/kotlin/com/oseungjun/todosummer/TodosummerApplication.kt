package com.oseungjun.todosummer

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.oseungjun.todosummer.core.data.preferences.LanguagePreferences
import com.oseungjun.todosummer.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.Locale

class TodosummerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin with Android context so data layer can resolve Context
        runCatching {
            startKoin {
                androidContext(this@TodosummerApplication)
                modules(appModules())
            }
        }
    }
    
    override fun attachBaseContext(base: Context) {
        // 앱 내 언어 설정에 따라 Locale 적용
        val locale = LanguagePreferences.getLocale(base)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(base.createConfigurationContext(config))
    }
}
