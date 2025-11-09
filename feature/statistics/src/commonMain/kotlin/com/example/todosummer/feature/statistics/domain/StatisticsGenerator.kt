package com.example.todosummer.feature.statistics.domain

import kotlinx.coroutines.flow.Flow

/**
 * 통계 생성을 위한 인터페이스
 */
interface StatisticsGenerator {
    /**
     * 주어진 데이터에 대한 통계를 생성합니다.
     * @param text 통계를 생성할 원본 데이터
     * @param maxLength 최대 결과 길이 (문자 수)
     * @return 생성된 통계 결과
     */
    suspend fun generateStatistics(text: String, maxLength: Int = 100): Result<String>
    
    /**
     * 통계 생성 과정을 스트림으로 반환합니다.
     * @param text 통계를 생성할 원본 데이터
     * @param maxLength 최대 결과 길이 (문자 수)
     * @return 통계 결과 스트림
     */
    fun generateStatisticsStream(text: String, maxLength: Int = 100): Flow<String>
    
    /**
     * 현재 모델이 로드되어 있는지 확인합니다.
     */
    fun isModelLoaded(): Boolean
    
    /**
     * 모델을 로드합니다.
     */
    suspend fun loadModel(): Result<Boolean>
}
