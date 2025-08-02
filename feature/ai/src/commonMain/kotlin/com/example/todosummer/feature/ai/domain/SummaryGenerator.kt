package com.example.todosummer.feature.ai.domain

import kotlinx.coroutines.flow.Flow

/**
 * AI 요약 생성을 위한 인터페이스
 */
interface SummaryGenerator {
    /**
     * 주어진 텍스트에 대한 요약을 생성합니다.
     * @param text 요약할 텍스트
     * @param maxLength 최대 요약 길이 (문자 수)
     * @return 요약 텍스트
     */
    suspend fun generateSummary(text: String, maxLength: Int = 100): Result<String>
    
    /**
     * 요약 생성 과정을 스트림으로 반환합니다.
     * @param text 요약할 텍스트
     * @param maxLength 최대 요약 길이 (문자 수)
     * @return 요약 텍스트 스트림
     */
    fun generateSummaryStream(text: String, maxLength: Int = 100): Flow<String>
    
    /**
     * 현재 모델이 로드되어 있는지 확인합니다.
     */
    fun isModelLoaded(): Boolean
    
    /**
     * 모델을 로드합니다.
     */
    suspend fun loadModel(): Result<Boolean>
}
