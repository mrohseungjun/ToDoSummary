package com.example.todosummer

import android.app.Application
import com.example.todosummer.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

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
}
