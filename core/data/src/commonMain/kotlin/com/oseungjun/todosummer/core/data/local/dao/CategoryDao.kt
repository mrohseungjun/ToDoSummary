package com.oseungjun.todosummer.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.oseungjun.todosummer.core.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Category 테이블에 대한 데이터 액세스 객체
 */
@Dao
interface CategoryDao {
    /**
     * 모든 카테고리를 가져옵니다 (Flow로 실시간 업데이트)
     */
    @Query("SELECT * FROM categories ORDER BY createdAt ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>
    
    /**
     * 특정 카테고리를 ID로 가져옵니다
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: String): CategoryEntity?
    
    /**
     * 새 카테고리를 추가합니다
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)
    
    /**
     * 여러 카테고리를 한 번에 추가합니다
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)
    
    /**
     * 카테고리를 삭제합니다
     */
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
    
    /**
     * 모든 카테고리를 삭제합니다
     */
    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}
