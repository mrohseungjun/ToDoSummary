package com.oseungjun.todosummer.core.data.source

import com.oseungjun.todosummer.core.data.local.dao.TodoDao
import com.oseungjun.todosummer.core.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * Room 기반 TodoDataSource 구현체
 */
class RoomTodoDataSource(
    private val dao: TodoDao
) : TodoDataSource {

    override fun getTodos(): Flow<List<TodoEntity>> {
        return dao.getAllTodos()
    }

    override suspend fun getTodoById(id: String): TodoEntity? {
        return dao.getTodoById(id)
    }

    override suspend fun addTodo(todo: TodoEntity): String {
        val id = if (todo.id.isBlank()) generateId() else todo.id
        dao.insertTodo(todo.copy(id = id))
        return id
    }

    override suspend fun updateTodo(todo: TodoEntity): Boolean {
        dao.updateTodo(todo)
        return true
    }

    override suspend fun deleteTodo(id: String): Boolean {
        dao.deleteTodoById(id)
        return true
    }

    private fun generateId(): String {
        val now = Clock.System.now().toEpochMilliseconds()
        val rnd = Random.nextInt(0, Int.MAX_VALUE)
        return "t-${now}-${rnd}"
    }
}
