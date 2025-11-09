package com.example.todosummer.feature.todo.di

import com.example.todosummer.feature.todo.presentation.TodoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val todoPresentationModule = module {
    viewModelOf(::TodoViewModel)
}
