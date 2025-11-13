package com.example.todosummer.core.data.repository

import com.example.todosummer.core.data.local.entity.toDomain
import com.example.todosummer.core.data.local.entity.toEntity
import com.example.todosummer.core.data.source.TodoDataSource
import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * TodoRepository 인터페이스의 구현체
 */
class TodoRepositoryImpl(
    private val dataSource: TodoDataSource
) : TodoRepository {
    
    override fun getTodos(): Flow<List<Todo>> {
        return dataSource.getTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getTodoById(id: String): Todo? {
        return dataSource.getTodoById(id)?.toDomain()
    }
    
    override suspend fun addTodo(todo: Todo): String {
        println("[TodoRepository] Adding todo: id=${todo.id}, hasReminder=${todo.hasReminder}, reminderTime=${todo.reminderTime}")
        return dataSource.addTodo(todo.toEntity())
    }
    
    override suspend fun updateTodo(todo: Todo): Boolean {
        println("[TodoRepository] Updating todo: id=${todo.id}, hasReminder=${todo.hasReminder}, reminderTime=${todo.reminderTime}")
        return dataSource.updateTodo(todo.toEntity())
    }
    
    override suspend fun deleteTodo(id: String): Boolean {
        return dataSource.deleteTodo(id)
    }
    
    override suspend fun toggleTodoCompletion(id: String): Boolean {
        val todo = dataSource.getTodoById(id) ?: return false
        val updatedTodo = todo.copy(isCompleted = !todo.isCompleted)
        return dataSource.updateTodo(updatedTodo)
    }
}
