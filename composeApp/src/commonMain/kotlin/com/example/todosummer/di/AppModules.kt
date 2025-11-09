package com.example.todosummer.di

import com.example.todosummer.core.data.di.dataModule
import com.example.todosummer.core.domain.di.DomainDI
import com.example.todosummer.core.domain.repository.TodoRepository
import com.example.todosummer.core.domain.usecase.TodoUseCases
import com.example.todosummer.feature.statistics.di.statisticsModule
import com.example.todosummer.feature.todo.di.todoPresentationModule
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
    statisticsModule // Statistics Feature ViewModel
)
