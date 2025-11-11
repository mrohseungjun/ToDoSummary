package com.example.todosummer.feature.todo.di

import com.example.todosummer.feature.todo.presentation.TodoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val todoPresentationModule = module {
    viewModel { TodoViewModel(get(), get()) }
}
