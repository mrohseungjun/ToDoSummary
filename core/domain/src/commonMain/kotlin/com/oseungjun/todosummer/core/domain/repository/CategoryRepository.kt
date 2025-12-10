package com.oseungjun.todosummer.core.domain.repository

import com.oseungjun.todosummer.core.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * 카테고리 데이터 접근을 위한 리포지토리 인터페이스
 */
interface CategoryRepository {
    /**
     * 모든 카테고리를 가져옵니다
     */
    fun getAllCategories(): Flow<List<Category>>
    
    /**
     * 새 카테고리를 추가합니다
     */
    suspend fun addCategory(category: Category)
    
    /**
     * 카테고리를 삭제합니다
     */
    suspend fun deleteCategory(category: Category)
}
