package com.example.todosummer.feature.todo.presentation

import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.domain.usecase.TodoUseCases
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Todo 화면의 상태를 관리하는 ViewModel
 */
class TodoViewModel(
    private val useCases: TodoUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = _state.asStateFlow()
    
    init {
        // 초기 로드: 리포지토리 Flow 수집
        viewModelScope.launch {
            useCases.getTodos().collect { todos ->
                _state.update { it.copy(isLoading = false, todos = todos) }
            }
        }
    }
    
    /**
     * 새로운 Todo 항목을 추가합니다.
     */
    fun addTodo(title: String, description: String?, priority: Priority, dueDate: LocalDateTime? = null) {
        if (title.isBlank()) return
        
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val todo = Todo(
            id = "",
            title = title,
            description = description,
            isCompleted = false,
            createdAt = now,
            updatedAt = null,
            dueDate = dueDate,
            priority = priority,
            tags = emptyList()
        )
        
        viewModelScope.launch {
            useCases.addTodo(todo)
        }
    }
    
    /**
     * Todo 항목을 삭제합니다.
     */
    fun deleteTodo(id: String) {
        viewModelScope.launch {
            try {
                useCases.deleteTodo(id)
            } catch (e: Exception) {
                _state.update { it.copy(error = "삭제 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }
    
    /**
     * Todo 항목의 완료 상태를 토글합니다.
     */
    fun toggleTodoCompletion(id: String) {
        viewModelScope.launch {
            try {
                useCases.toggleTodoCompletion(id)
            } catch (e: Exception) {
                _state.update { it.copy(error = "상태 변경 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }
    
    /**
     * Todo 항목을 업데이트합니다.
     */
    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                val updatedTodo = todo.copy(
                    updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
                useCases.updateTodo(updatedTodo)
            } catch (e: Exception) {
                _state.update { it.copy(error = "업데이트 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }

    /**
     * 인텐트 기반 액션 처리
     */
    fun onIntent(intent: TodoIntent) {
        when (intent) {
            is TodoIntent.Add -> addTodo(
                title = intent.title,
                description = intent.description,
                priority = intent.priority,
                dueDate = intent.dueDate
            )
            is TodoIntent.Update -> updateTodo(intent.todo)
            is TodoIntent.Delete -> deleteTodo(intent.id)
            is TodoIntent.Toggle -> toggleTodoCompletion(intent.id)
            TodoIntent.Load -> { /* 초기 수집으로 대체됨 */ }
        }
    }
}

