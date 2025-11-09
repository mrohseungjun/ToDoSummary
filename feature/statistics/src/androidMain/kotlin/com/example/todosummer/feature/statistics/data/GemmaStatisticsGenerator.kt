package com.example.todosummer.feature.statistics.data

import android.content.Context
import com.example.todosummer.feature.statistics.domain.StatisticsGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 임시 통계 생성기 구현 (ML Kit API 미배포로 인해 임시 구현)
 */
class GemmaStatisticsGenerator(
    private val context: Context,
    private val apiKey: String
) : StatisticsGenerator {

    private var isLoaded = false

    override suspend fun generateStatistics(text: String, maxLength: Int): Result<String> {
        return Result.failure(Exception("통계 기능이 일시적으로 비활성화되었습니다"))
    }

    override fun generateStatisticsStream(text: String, maxLength: Int): Flow<String> {
        val flow = MutableStateFlow("통계 기능이 일시적으로 비활성화되었습니다")
        return flow.asStateFlow()
    }

    override fun isModelLoaded(): Boolean {
        return false
    }

    override suspend fun loadModel(): Result<Boolean> {
        return Result.failure(Exception("통계 기능이 일시적으로 비활성화되었습니다"))
    }

    /**
     * 리소스 해제
     */
    fun close() {
        isLoaded = false
    }
}
