package com.oseungjun.todosummer.di

import com.oseungjun.todosummer.feature.statistics.data.GemmaStatisticsGenerator
import com.oseungjun.todosummer.feature.statistics.domain.StatisticsGenerator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<StatisticsGenerator> {
        GemmaStatisticsGenerator(
            context = androidContext(),
            apiKey = ""
        )
    }
}
