package com.oseungjun.todosummer.core.data.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoinIos(vararg extraModules: Module): KoinApplication = startKoin {
    modules(listOf(dataModule) + extraModules)
}
