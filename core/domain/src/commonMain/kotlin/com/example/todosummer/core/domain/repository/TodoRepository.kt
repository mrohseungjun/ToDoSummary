package com.example.todosummer.core.domain.repository

import com.example.todosummer.core.domain.model.Todo
import kotlinx.coroutines.flow.Flow

/**
 * Todo 항목을 관리하는 리포지토리 인터페이스
 */
interface TodoRepository {
    /**
     * 모든 Todo 항목을 Flow로 가져옵니다.
     */
    fun getTodos(): Flow<List<Todo>>
    
    /**
     * ID로 특정 Todo 항목을 가져옵니다.
     */
    suspend fun getTodoById(id: String): Todo?
    
    /**
     * 새로운 Todo 항목을 추가합니다.
     */
    suspend fun addTodo(todo: Todo): String
    
    /**
     * 기존 Todo 항목을 업데이트합니다.
     */
    suspend fun updateTodo(todo: Todo): Boolean
    
    /**
     * Todo 항목을 삭제합니다.
     */
    suspend fun deleteTodo(id: String): Boolean
    
    /**
     * Todo 항목의 완료 상태를 토글합니다.
     */
    suspend fun toggleTodoCompletion(id: String): Boolean
}
