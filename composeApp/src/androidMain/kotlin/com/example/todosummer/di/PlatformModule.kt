package com.example.todosummer.di

import com.example.todosummer.feature.statistics.data.GemmaStatisticsGenerator
import com.example.todosummer.feature.statistics.domain.StatisticsGenerator
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
