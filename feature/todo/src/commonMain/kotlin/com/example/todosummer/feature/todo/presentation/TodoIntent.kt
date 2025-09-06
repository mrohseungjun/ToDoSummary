package com.example.todosummer.feature.todo.presentation

import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import kotlinx.datetime.LocalDateTime

sealed interface TodoIntent {
    data object Load : TodoIntent

    data class Add(
        val title: String,
        val description: String?,
        val priority: Priority,
        val dueDate: LocalDateTime?
    ) : TodoIntent

    data class Update(val todo: Todo) : TodoIntent
    data class Delete(val id: String) : TodoIntent
    data class Toggle(val id: String) : TodoIntent
}
