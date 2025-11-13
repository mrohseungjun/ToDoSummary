package com.example.todosummer.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import kotlinx.datetime.LocalDateTime

/**
 * Todo Room 엔티티
 * Domain 모델과 분리하여 데이터베이스 스키마 변경에 유연하게 대응
 */
@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val priority: String, // Priority enum을 String으로 저장
    val createdAt: String, // LocalDateTime을 ISO String으로 저장
    val updatedAt: String?,
    val dueDate: String?,
    val category: String, // 카테고리
    val hasReminder: Boolean = false, // 알림 설정 여부
    val reminderTime: String? = null // 알림 시간
)

/**
 * TodoEntity를 Domain 모델로 변환
 */
fun TodoEntity.toDomain(): Todo = Todo(
    id = id,
    title = title,
    isCompleted = isCompleted,
    priority = Priority.valueOf(priority),
    createdAt = LocalDateTime.parse(createdAt),
    updatedAt = updatedAt?.let { LocalDateTime.parse(it) },
    dueDate = dueDate?.let { LocalDateTime.parse(it) },
    category = category,
    hasReminder = hasReminder,
    reminderTime = reminderTime?.let { LocalDateTime.parse(it) }
)

/**
 * Domain 모델을 TodoEntity로 변환
 */
fun Todo.toEntity(): TodoEntity = TodoEntity(
    id = id,
    title = title,
    isCompleted = isCompleted,
    priority = priority.name,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt?.toString(),
    dueDate = dueDate?.toString(),
    category = category,
    hasReminder = hasReminder,
    reminderTime = reminderTime?.toString()
)
