package com.example.todosummer.core.domain.ai

import com.example.todosummer.core.domain.model.Todo

/**
 * AI 리포트 결과
 */
data class AIReport(
    val summary: String = "",
    val insights: List<String> = emptyList(),
    val actionItems: List<String> = emptyList(),
    val procrastinationPatterns: ProcrastinationPatterns? = null
)

/**
 * 미루기 패턴 분석 결과
 */
data class ProcrastinationPatterns(
    val frequentCategories: List<String> = emptyList(),
    val frequentTimeSlots: List<String> = emptyList(),
    val aiComment: String = ""
)

/**
 * Gemini AI 서비스 인터페이스
 */
interface GeminiService {
    /**
     * 주간/월간 AI 리포트 생성
     */
    suspend fun generateReport(
        todos: List<Todo>,
        periodLabel: String
    ): Result<AIReport>
    
    /**
     * 미루기 패턴 분석
     */
    suspend fun analyzeProcrastination(
        todos: List<Todo>
    ): Result<ProcrastinationPatterns>
}
