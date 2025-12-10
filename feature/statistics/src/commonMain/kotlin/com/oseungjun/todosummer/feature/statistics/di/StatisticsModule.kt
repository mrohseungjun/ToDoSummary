package com.oseungjun.todosummer.feature.statistics.di

import com.oseungjun.todosummer.feature.statistics.presentation.StatisticsViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val statisticsModule = module {
    viewModel { StatisticsViewModel(get(), getOrNull()) }
}
