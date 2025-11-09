package com.example.todosummer.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todosummer.core.domain.model.Category
import kotlinx.datetime.LocalDateTime

/**
 * Category Room 엔티티
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val createdAt: String // LocalDateTime을 ISO String으로 저장
)

/**
 * CategoryEntity를 Domain 모델로 변환
 */
fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    createdAt = LocalDateTime.parse(createdAt)
)

/**
 * Domain 모델을 CategoryEntity로 변환
 */
fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    createdAt = createdAt.toString()
)
