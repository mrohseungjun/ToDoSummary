package com.oseungjun.todosummer.core.data.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoinAndroid(appContext: Context, vararg extraModules: Module): KoinApplication = startKoin {
    androidContext(appContext)
    modules(listOf(dataModule) + extraModules)
}
