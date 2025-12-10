package com.oseungjun.todosummer.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.oseungjun.todosummer.core.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Todo 데이터 접근 객체 (DAO)
 * Room을 통한 데이터베이스 작업 정의
 */
@Dao
interface TodoDao {
    
    /**
     * 모든 Todo 조회 (완료 상태별 정렬)
     */
    @Query("SELECT * FROM todos ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>
    
    /**
     * 완료되지 않은 Todo만 조회
     */
    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveTodos(): Flow<List<TodoEntity>>
    
    /**
     * 완료된 Todo만 조회
     */
    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY updatedAt DESC")
    fun getCompletedTodos(): Flow<List<TodoEntity>>
    
    /**
     * 우선순위별 Todo 조회
     */
    @Query("SELECT * FROM todos WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTodosByPriority(priority: String): Flow<List<TodoEntity>>
    
    /**
     * ID로 특정 Todo 조회
     */
    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: String): TodoEntity?
    
    /**
     * Todo 제목으로 검색
     */
    @Query("SELECT * FROM todos WHERE title LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTodos(query: String): Flow<List<TodoEntity>>
    
    /**
     * Todo 추가 (충돌 시 교체)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity)
    
    /**
     * 여러 Todo 추가
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<TodoEntity>)
    
    /**
     * Todo 업데이트
     */
    @Update
    suspend fun updateTodo(todo: TodoEntity)
    
    /**
     * Todo 삭제
     */
    @Delete
    suspend fun deleteTodo(todo: TodoEntity)
    
    /**
     * ID로 Todo 삭제
     */
    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodoById(id: String)
    
    /**
     * 완료된 모든 Todo 삭제
     */
    @Query("DELETE FROM todos WHERE isCompleted = 1")
    suspend fun deleteCompletedTodos()
    
    /**
     * 모든 Todo 삭제
     */
    @Query("DELETE FROM todos")
    suspend fun deleteAllTodos()
    
    /**
     * Todo 완료 상태 토글
     */
    @Query("UPDATE todos SET isCompleted = :isCompleted, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTodoCompletion(id: String, isCompleted: Boolean, updatedAt: String)
}
