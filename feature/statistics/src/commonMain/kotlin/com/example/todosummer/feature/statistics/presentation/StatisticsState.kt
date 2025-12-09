package com.example.todosummer.feature.statistics.presentation

/**
 * 통계 화면의 UI 상태
 */
data class StatisticsState(
    val isLoading: Boolean = true,
    val period: StatisticsPeriod = StatisticsPeriod.WEEK,
    val totalCompleted: Int = 0,
    val totalTodos: Int = 0,
    val completionRate: Float = 0f,
    val topCategory: String = "",
    val categoryDistribution: Map<String, Int> = emptyMap(),
    val trendData: List<Float> = emptyList(),
    val trendLabels: List<String> = emptyList(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val productivityScore: Int = 0,
    val insight: String = "",
    val error: String? = null,
    
    // AI 리포트 상태
    val isGeneratingAIReport: Boolean = false,
    val aiReportSummary: String = "",
    val aiReportInsights: List<String> = emptyList(),
    val aiReportActionItems: List<String> = emptyList(),
    val aiReportError: String? = null,
    
    // 미루기 패턴 분석 상태
    val isAnalyzingProcrastination: Boolean = false,
    val procrastinationCategories: List<String> = emptyList(),
    val procrastinationTimeSlots: List<String> = emptyList(),
    val procrastinationComment: String = "",
    val procrastinationError: String? = null,
    
    // AI 분석 횟수 제한 (하루 3회)
    val dailyAIUsageCount: Int = 0,
    val maxDailyAIUsage: Int = 3,
    val lastAIUsageDate: String = ""  // "yyyy-MM-dd" 형식
)

/**
 * 통계 기간
 */
enum class StatisticsPeriod {
    WEEK,    // 주간
    MONTH    // 월간
}
