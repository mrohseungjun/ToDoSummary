package com.example.todosummer.core.data.source

import com.example.todosummer.core.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Todo 데이터를 관리하는 데이터 소스 인터페이스
 */
interface TodoDataSource {
    /**
     * 모든 Todo 항목을 Flow로 가져옵니다.
     */
    fun getTodos(): Flow<List<TodoEntity>>
    
    /**
     * ID로 특정 Todo 항목을 가져옵니다.
     */
    suspend fun getTodoById(id: String): TodoEntity?
    
    /**
     * 새로운 Todo 항목을 추가합니다.
     */
    suspend fun addTodo(todo: TodoEntity): String
    
    /**
     * 기존 Todo 항목을 업데이트합니다.
     */
    suspend fun updateTodo(todo: TodoEntity): Boolean
    
    /**
     * Todo 항목을 삭제합니다.
     */
    suspend fun deleteTodo(id: String): Boolean
}
