package com.example.todosummer.di

import com.example.todosummer.core.data.di.dataModule
import com.example.todosummer.core.domain.di.DomainDI
import com.example.todosummer.core.domain.repository.TodoRepository
import com.example.todosummer.core.domain.usecase.TodoUseCases
import org.koin.dsl.module

// Domain 계층의 팩토리를 이용해 UseCases 바인딩을 app 계층에서 제공
val appDomainModule = module {
    factory<TodoUseCases> { DomainDI.provideTodoUseCases(get<TodoRepository>()) }
}

fun appModules() = listOf(
    dataModule, // Repository, DB, DataSource 등
    appDomainModule // UseCases
)
