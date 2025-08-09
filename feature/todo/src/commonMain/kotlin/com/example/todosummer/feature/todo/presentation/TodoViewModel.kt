package com.example.todosummer.feature.todo.presentation

import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.domain.usecase.TodoUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val todoUseCases: TodoUseCases,
    private val coroutineScope: CoroutineScope
) {
    private val _state = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = _state.asStateFlow()
    
    init {
        loadTodos()
    }
    
    /**
     * 모든 Todo 항목을 로드합니다.
     */
    fun loadTodos() {
        coroutineScope.launch {
            todoUseCases.getTodos().collect { todos ->
                _state.update { it.copy(todos = todos) }
            }
        }
    }
    
    /**
     * 새로운 Todo 항목을 추가합니다.
     */
    fun addTodo(title: String, description: String, priority: Priority, dueDate: LocalDateTime? = null) {
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
        
        coroutineScope.launch {
            todoUseCases.addTodo(todo)
        }
    }
    
    /**
     * Todo 항목을 삭제합니다.
     */
    fun deleteTodo(id: String) {
        coroutineScope.launch {
            try {
                todoUseCases.deleteTodo(id)
                loadTodos() // 삭제 후 목록 새로고침
            } catch (e: Exception) {
                _state.update { it.copy(error = "삭제 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }
    
    /**
     * Todo 항목의 완료 상태를 토글합니다.
     */
    fun toggleTodoCompletion(id: String) {
        coroutineScope.launch {
            try {
                todoUseCases.toggleTodoCompletion(id)
                loadTodos() // 상태 변경 후 목록 새로고침
            } catch (e: Exception) {
                _state.update { it.copy(error = "상태 변경 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }
    
    /**
     * Todo 항목을 업데이트합니다.
     */
    fun updateTodo(todo: Todo) {
        coroutineScope.launch {
            try {
                val updatedTodo = todo.copy(
                    updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
                todoUseCases.updateTodo(updatedTodo)
                loadTodos() // 업데이트 후 목록 새로고침
            } catch (e: Exception) {
                _state.update { it.copy(error = "업데이트 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }
}

/**
 * Todo 화면의 상태
 */
data class TodoState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
