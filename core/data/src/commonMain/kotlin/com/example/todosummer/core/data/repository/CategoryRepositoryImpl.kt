package com.example.todosummer.core.data.repository

import com.example.todosummer.core.data.local.dao.CategoryDao
import com.example.todosummer.core.data.local.entity.toDomain
import com.example.todosummer.core.data.local.entity.toEntity
import com.example.todosummer.core.domain.model.Category
import com.example.todosummer.core.domain.repository.CategoryRepository
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
