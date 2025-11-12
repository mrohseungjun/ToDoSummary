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
    val error: String? = null
)

/**
 * 통계 기간
 */
enum class StatisticsPeriod {
    WEEK,    // 주간
    MONTH,   // 월간
    ALL      // 전체
}
