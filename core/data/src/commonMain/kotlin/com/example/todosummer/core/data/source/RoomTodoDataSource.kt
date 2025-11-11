package com.example.todosummer.core.data.source

import com.example.todosummer.core.data.local.dao.TodoDao
import com.example.todosummer.core.data.local.entity.TodoEntity as LocalTodoEntity
import com.example.todosummer.core.data.model.TodoEntity as DataTodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * Room 기반 TodoDataSource 구현체
 */
class RoomTodoDataSource(
    private val dao: TodoDao
) : TodoDataSource {

    override fun getTodos(): Flow<List<DataTodoEntity>> {
        return dao.getAllTodos().map { list ->
            list.map { it.toDataModel() }
        }
    }

    override suspend fun getTodoById(id: String): DataTodoEntity? {
        return dao.getTodoById(id)?.toDataModel()
    }

    override suspend fun addTodo(todo: DataTodoEntity): String {
        val id = if (todo.id.isBlank()) generateId() else todo.id
        dao.insertTodo(todo.copy(id = id).toLocalEntity())
        return id
    }

    override suspend fun updateTodo(todo: DataTodoEntity): Boolean {
        dao.updateTodo(todo.toLocalEntity())
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

// core/data/.../RoomTodoDataSource.kt
private fun LocalTodoEntity.toDataModel(): DataTodoEntity = DataTodoEntity(
    id = id,
    title = title,
    isCompleted = isCompleted,
    createdAt = createdAt,
    updatedAt = updatedAt,
    dueDate = dueDate,          // ✅ 추가
    priority = priority,
    category = category
)

private fun DataTodoEntity.toLocalEntity(): LocalTodoEntity = LocalTodoEntity(
    id = id,
    title = title,
    isCompleted = isCompleted,
    priority = priority,
    createdAt = createdAt,
    updatedAt = updatedAt ?: createdAt,
    dueDate = dueDate,
    category = category
)
