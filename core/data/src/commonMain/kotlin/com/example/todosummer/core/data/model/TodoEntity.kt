package com.example.todosummer.core.data.model

import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * Todo 항목의 데이터 모델
 */
@Serializable
data class TodoEntity(
    val id: String,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val createdAt: String, // LocalDateTime을 String으로 저장
    val updatedAt: String? = null,
    val dueDate: String? = null,
    val priority: String, // Enum을 String으로 저장
    val tags: List<String> = emptyList()
) {
    /**
     * TodoEntity를 도메인 모델인 Todo로 변환
     */
    fun toDomain(): Todo {
        return Todo(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            createdAt = LocalDateTime.parse(createdAt),
            updatedAt = updatedAt?.let { LocalDateTime.parse(it) },
            dueDate = dueDate?.let { LocalDateTime.parse(it) },
            priority = Priority.valueOf(priority),
            tags = tags
        )
    }
    
    companion object {
        /**
         * 도메인 모델인 Todo를 TodoEntity로 변환
         */
        fun fromDomain(todo: Todo): TodoEntity {
            return TodoEntity(
                id = todo.id,
                title = todo.title,
                description = todo.description,
                isCompleted = todo.isCompleted,
                createdAt = todo.createdAt.toString(),
                updatedAt = todo.updatedAt?.toString(),
                dueDate = todo.dueDate?.toString(),
                priority = todo.priority.name,
                tags = todo.tags
            )
        }
    }
}
