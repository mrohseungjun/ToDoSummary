package com.oseungjun.todosummer.feature.todo.di

import com.oseungjun.todosummer.feature.todo.presentation.TodoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val todoPresentationModule = module {
    viewModel { TodoViewModel(get(), get(), get()) }
}
