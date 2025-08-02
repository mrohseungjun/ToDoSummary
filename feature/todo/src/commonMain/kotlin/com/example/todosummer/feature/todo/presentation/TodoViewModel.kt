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
            todoUseCases.GetTodosUseCase(todoUseCases.repository).invoke().collect { todos ->
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
            todoUseCases.AddTodoUseCase(todoUseCases.repository).invoke(todo)
        }
    }
    
    /**
     * Todo 항목을 삭제합니다.
     */
    fun deleteTodo(id: String) {
        coroutineScope.launch {
            todoUseCases.DeleteTodoUseCase(todoUseCases.repository).invoke(id)
        }
    }
    
    /**
     * Todo 항목의 완료 상태를 토글합니다.
     */
    fun toggleTodoCompletion(id: String) {
        coroutineScope.launch {
            todoUseCases.ToggleTodoCompletionUseCase(todoUseCases.repository).invoke(id)
        }
    }
    
    /**
     * Todo 항목을 업데이트합니다.
     */
    fun updateTodo(todo: Todo) {
        coroutineScope.launch {
            val updatedTodo = todo.copy(
                updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
            todoUseCases.UpdateTodoUseCase(todoUseCases.repository).invoke(updatedTodo)
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
