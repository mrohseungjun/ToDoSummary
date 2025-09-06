package com.example.todosummer.core.data.di

import com.example.todosummer.core.data.local.database.TodoDatabase
import com.example.todosummer.core.data.local.dao.TodoDao
import com.example.todosummer.core.data.repository.TodoRepositoryImpl
import com.example.todosummer.core.data.source.TodoDataSource
import com.example.todosummer.core.domain.repository.TodoRepository
import org.koin.dsl.module

val dataModule = module {
    // Database & DAO
    single<TodoDatabase> { com.example.todosummer.core.data.local.database.createTodoDatabase() }
    single<TodoDao> { get<TodoDatabase>().todoDao() }

    // DataSource (Room-backed)
    single<TodoDataSource> { com.example.todosummer.core.data.source.RoomTodoDataSource(get()) }

    // Repository
    single<TodoRepository> { TodoRepositoryImpl(get()) }
}
