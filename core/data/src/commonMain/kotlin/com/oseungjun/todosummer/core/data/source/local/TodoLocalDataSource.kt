package com.oseungjun.todosummer.core.data.source.local

import com.oseungjun.todosummer.core.data.local.entity.TodoEntity
import com.oseungjun.todosummer.core.data.source.TodoDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Todo 데이터를 로컬에서 관리하는 데이터 소스 구현체
 * 실제 구현에서는 플랫폼별 영구 저장소(SQLite, Realm 등)를 사용해야 합니다.
 * 이 구현은 메모리 내 저장을 위한 임시 구현입니다.
 */
class TodoLocalDataSource : TodoDataSource {
    
    private val _todos = MutableStateFlow<Map<String, TodoEntity>>(emptyMap())
    
    override fun getTodos(): Flow<List<TodoEntity>> {
        return _todos.asStateFlow().map { it.values.toList() }
    }
    
    override suspend fun getTodoById(id: String): TodoEntity? {
        return _todos.value[id]
    }
    
    override suspend fun addTodo(todo: TodoEntity): String {
        val id = todo.id.ifEmpty { generateId() }
        val todoWithId = if (todo.id.isEmpty()) todo.copy(id = id) else todo
        
        _todos.update { currentTodos ->
            currentTodos + (id to todoWithId)
        }
        
        return id
    }
    
    override suspend fun updateTodo(todo: TodoEntity): Boolean {
        if (!_todos.value.containsKey(todo.id)) {
            return false
        }
        
        _todos.update { currentTodos ->
            currentTodos + (todo.id to todo)
        }
        
        return true
    }
    
    override suspend fun deleteTodo(id: String): Boolean {
        if (!_todos.value.containsKey(id)) {
            return false
        }
        
        _todos.update { currentTodos ->
            currentTodos - id
        }
        
        return true
    }
    
    private fun generateId(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }
    
    companion object {
        fun getCurrentDateTime(): LocalDateTime {
            return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }
}
