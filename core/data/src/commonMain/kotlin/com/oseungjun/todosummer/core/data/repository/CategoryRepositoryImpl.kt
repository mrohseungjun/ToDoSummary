package com.oseungjun.todosummer.core.data.repository

import com.oseungjun.todosummer.core.data.local.dao.CategoryDao
import com.oseungjun.todosummer.core.data.local.entity.toDomain
import com.oseungjun.todosummer.core.data.local.entity.toEntity
import com.oseungjun.todosummer.core.domain.model.Category
import com.oseungjun.todosummer.core.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * CategoryRepository 구현체
 */
class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    
    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun addCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }
    
    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }
}
