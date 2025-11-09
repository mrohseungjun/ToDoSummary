package com.example.todosummer.feature.todo.presentation

import com.example.todosummer.core.domain.model.Category
import com.example.todosummer.core.domain.model.Todo

// UI 상태: 불변 데이터 클래스 + StateFlow로 노출
// unstable mutable 컬렉션 금지: List<Todo> 사용
data class TodoState(
    val isLoading: Boolean = true,
    val todos: List<Todo> = emptyList(),
    val categories: List<Category> = emptyList(),
    val error: String? = null
)
