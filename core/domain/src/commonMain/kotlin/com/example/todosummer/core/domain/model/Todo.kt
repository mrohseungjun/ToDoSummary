package com.example.todosummer.core.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Todo 항목을 나타내는 도메인 모델
 */
data class Todo(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val createdAt: LocalDateTime,  // 생성 일시
    val updatedAt: LocalDateTime?,  // 수정 일시
    val dueDate: LocalDateTime?,  // 마감 일시 (선택사항)
    val priority: Priority,
    val category: String = "업무", // 기본 카테고리
    val hasReminder: Boolean = false,  // 알림 설정 여부
    val reminderTime: LocalDateTime? = null  // 알림 시간
)

enum class Priority {
    LOW, MEDIUM, HIGH
}
