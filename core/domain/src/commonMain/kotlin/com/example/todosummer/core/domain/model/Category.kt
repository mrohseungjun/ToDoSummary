package com.example.todosummer.core.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * 카테고리를 나타내는 도메인 모델
 */
data class Category(
    val id: String,
    val name: String,
    val createdAt: LocalDateTime
)
