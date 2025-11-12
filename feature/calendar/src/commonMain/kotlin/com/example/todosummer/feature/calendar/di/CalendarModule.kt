package com.example.todosummer.feature.calendar.di

import com.example.todosummer.feature.calendar.presentation.CalendarViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * 캘린더 모듈의 Koin DI 설정
 */
val calendarModule = module {
    viewModel { CalendarViewModel(useCases = get(), categoryRepository = get()) }
}
