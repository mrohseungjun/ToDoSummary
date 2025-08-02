package com.example.todosummer.core.domain.usecase

import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

/**
 * Todo 관련 유스케이스를 모아둔 클래스
 */
class TodoUseCases(
    private val repository: TodoRepository
) {
    /**
     * 모든 Todo 항목을 가져오는 유스케이스
     */
    class GetTodosUseCase(private val repository: TodoRepository) {
        operator fun invoke(): Flow<List<Todo>> {
            return repository.getTodos()
        }
    }
    
    /**
     * ID로 특정 Todo 항목을 가져오는 유스케이스
     */
    class GetTodoByIdUseCase(private val repository: TodoRepository) {
        suspend operator fun invoke(id: String): Todo? {
            return repository.getTodoById(id)
        }
    }
    
    /**
     * 새로운 Todo 항목을 추가하는 유스케이스
     */
    class AddTodoUseCase(private val repository: TodoRepository) {
        suspend operator fun invoke(todo: Todo): String {
            return repository.addTodo(todo)
        }
    }
    
    /**
     * Todo 항목을 업데이트하는 유스케이스
     */
    class UpdateTodoUseCase(private val repository: TodoRepository) {
        suspend operator fun invoke(todo: Todo): Boolean {
            return repository.updateTodo(todo)
        }
    }
    
    /**
     * Todo 항목을 삭제하는 유스케이스
     */
    class DeleteTodoUseCase(private val repository: TodoRepository) {
        suspend operator fun invoke(id: String): Boolean {
            return repository.deleteTodo(id)
        }
    }
    
    /**
     * Todo 항목의 완료 상태를 토글하는 유스케이스
     */
    class ToggleTodoCompletionUseCase(private val repository: TodoRepository) {
        suspend operator fun invoke(id: String): Boolean {
            return repository.toggleTodoCompletion(id)
        }
    }
}
