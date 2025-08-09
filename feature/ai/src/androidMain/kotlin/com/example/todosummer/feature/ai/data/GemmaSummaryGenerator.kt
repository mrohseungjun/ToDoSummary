package com.example.todosummer.feature.ai.data

import android.content.Context
import com.example.todosummer.feature.ai.domain.SummaryGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 임시 요약 생성기 구현 (ML Kit API 미배포로 인해 임시 구현)
 */
class GemmaSummaryGenerator(
    private val context: Context,
    private val apiKey: String
) : SummaryGenerator {
    
    private var isLoaded = false
    
    override suspend fun generateSummary(text: String, maxLength: Int): Result<String> {
        return Result.failure(Exception("AI 요약 기능이 일시적으로 비활성화되었습니다"))
    }
    
    override fun generateSummaryStream(text: String, maxLength: Int): Flow<String> {
        val flow = MutableStateFlow("AI 요약 기능이 일시적으로 비활성화되었습니다")
        return flow.asStateFlow()
    }
    
    override fun isModelLoaded(): Boolean {
        return false
    }
    
    override suspend fun loadModel(): Result<Boolean> {
        return Result.failure(Exception("AI 요약 기능이 일시적으로 비활성화되었습니다"))
    }
    
    /**
     * 리소스 해제
     */
    fun close() {
        isLoaded = false
    }
}
