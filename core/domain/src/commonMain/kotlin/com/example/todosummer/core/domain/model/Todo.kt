package com.example.todosummer.core.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Todo 항목을 나타내는 도메인 모델
 */
data class Todo(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val dueDate: LocalDateTime?,
    val priority: Priority,
    val tags: List<String> = emptyList()
)

enum class Priority {
    LOW, MEDIUM, HIGH
}
