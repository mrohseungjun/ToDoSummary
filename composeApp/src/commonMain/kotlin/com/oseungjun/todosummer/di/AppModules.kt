package com.oseungjun.todosummer.di

import com.oseungjun.todosummer.core.data.di.dataModule
import com.oseungjun.todosummer.core.domain.di.DomainDI
import com.oseungjun.todosummer.core.domain.repository.TodoRepository
import com.oseungjun.todosummer.core.domain.usecase.TodoUseCases
import com.oseungjun.todosummer.feature.calendar.di.calendarModule
import com.oseungjun.todosummer.feature.statistics.di.statisticsModule
import com.oseungjun.todosummer.feature.todo.di.todoPresentationModule
import org.koin.core.module.Module
import org.koin.dsl.module

// Platform-specific module
expect val platformModule: Module

// Domain 계층의 팩토리를 이용해 UseCases 바인딩을 app 계층에서 제공
val appDomainModule = module {
    factory<TodoUseCases> { DomainDI.provideTodoUseCases(get<TodoRepository>()) }
}

fun appModules() = listOf(
    platformModule,
    dataModule, // Repository, DB, DataSource 등
    appDomainModule, // UseCases
    todoPresentationModule, // Todo Feature ViewModel
    calendarModule, // Calendar Feature ViewModel
    statisticsModule // Statistics Feature ViewModel
)
