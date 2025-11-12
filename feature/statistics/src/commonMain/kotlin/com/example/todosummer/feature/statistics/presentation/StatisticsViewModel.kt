package com.example.todosummer.feature.statistics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todosummer.core.domain.usecase.TodoUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/**
 * 활동 리포트 통계를 위한 ViewModel
 */
class StatisticsViewModel(
    private val useCases: TodoUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            useCases.getTodos().collect { todos ->
                val period = _state.value.period
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                
                // 기간별 필터링
                val filteredTodos = when (period) {
                    StatisticsPeriod.WEEK -> {
                        val weekAgo = now.date.minus(7, DateTimeUnit.DAY)
                        todos.filter { it.createdAt.date >= weekAgo }
                    }
                    StatisticsPeriod.MONTH -> {
                        val monthAgo = now.date.minus(30, DateTimeUnit.DAY)
                        todos.filter { it.createdAt.date >= monthAgo }
                    }
                    StatisticsPeriod.ALL -> todos
                }
                
                // 총 완료
                val totalCompleted = filteredTodos.count { it.isCompleted }
                val totalTodos = filteredTodos.size
                
                // 완료율
                val completionRate = if (filteredTodos.isNotEmpty()) {
                    totalCompleted.toFloat() / filteredTodos.size.toFloat()
                } else 0f
                
                // 최다 카테고리
                val topCategory = filteredTodos
                    .groupBy { it.category }
                    .maxByOrNull { it.value.size }
                    ?.key ?: ""
                
                // 카테고리 분포
                val categoryDistribution = filteredTodos
                    .groupBy { it.category }
                    .mapValues { it.value.size }
                
                // 기간별 추이 데이터 계산
                val (trendData, trendLabels) = when (period) {
                    StatisticsPeriod.WEEK -> {
                        // 최근 4주 완료율
                        val data = (0..3).map { weekOffset ->
                            val weekStart = now.date.minus(7 * (weekOffset + 1), DateTimeUnit.DAY)
                            val weekEnd = now.date.minus(7 * weekOffset, DateTimeUnit.DAY)
                            val weekTodos = todos.filter { 
                                it.createdAt.date >= weekStart && it.createdAt.date < weekEnd 
                            }
                            if (weekTodos.isNotEmpty()) {
                                weekTodos.count { it.isCompleted }.toFloat() / weekTodos.size.toFloat()
                            } else 0f
                        }.reversed()
                        val labels = listOf("3주 전", "2주 전", "1주 전", "이번 주")
                        data to labels
                    }
                    StatisticsPeriod.MONTH -> {
                        // 최근 4개월 완료율
                        val data = (0..3).map { monthOffset ->
                            val monthStart = now.date.minus(30 * (monthOffset + 1), DateTimeUnit.DAY)
                            val monthEnd = now.date.minus(30 * monthOffset, DateTimeUnit.DAY)
                            val monthTodos = todos.filter { 
                                it.createdAt.date >= monthStart && it.createdAt.date < monthEnd 
                            }
                            if (monthTodos.isNotEmpty()) {
                                monthTodos.count { it.isCompleted }.toFloat() / monthTodos.size.toFloat()
                            } else 0f
                        }.reversed()
                        val labels = listOf("3개월 전", "2개월 전", "1개월 전", "이번 달")
                        data to labels
                    }
                    StatisticsPeriod.ALL -> {
                        // 최근 6개월 총 완료 개수 (정규화)
                        val monthlyData = (0..5).map { monthOffset ->
                            val monthStart = now.date.minus(30 * (monthOffset + 1), DateTimeUnit.DAY)
                            val monthEnd = now.date.minus(30 * monthOffset, DateTimeUnit.DAY)
                            val monthTodos = todos.filter { 
                                it.createdAt.date >= monthStart && it.createdAt.date < monthEnd 
                            }
                            monthTodos.count { it.isCompleted }.toFloat()
                        }.reversed()
                        
                        // 최대값으로 정규화 (0~1)
                        val maxCount = monthlyData.maxOrNull() ?: 1f
                        val normalizedData = if (maxCount > 0) {
                            monthlyData.map { it / maxCount }
                        } else {
                            monthlyData
                        }
                        
                        val labels = listOf("5개월 전", "4개월 전", "3개월 전", "2개월 전", "1개월 전", "이번 달")
                        normalizedData to labels
                    }
                }
                
                // 연속 달성 스트릭 계산
                val sortedByDate = todos.sortedByDescending { it.createdAt.date }
                var currentStreak = 0
                var longestStreak = 0
                var tempStreak = 0
                var lastDate = now.date
                
                sortedByDate.forEach { todo ->
                    if (todo.isCompleted) {
                        val daysDiff = lastDate.minus(todo.createdAt.date).days
                        if (daysDiff <= 1) {
                            tempStreak++
                            if (todo.createdAt.date == now.date || todo.createdAt.date == now.date.minus(1, DateTimeUnit.DAY)) {
                                currentStreak = tempStreak
                            }
                        } else {
                            if (tempStreak > longestStreak) longestStreak = tempStreak
                            tempStreak = 1
                        }
                        lastDate = todo.createdAt.date
                    }
                }
                if (tempStreak > longestStreak) longestStreak = tempStreak
                
                // 생산성 점수 (0-100)
                val productivityScore = ((completionRate * 50) + 
                    (currentStreak.coerceAtMost(10) * 3) + 
                    (categoryDistribution.size.coerceAtMost(5) * 4)).toInt().coerceIn(0, 100)
                
                // 인사이트 메시지
                val insight = generateInsight(completionRate, currentStreak, trendData)
                
                _state.update {
                    it.copy(
                        isLoading = false,
                        totalCompleted = totalCompleted,
                        totalTodos = totalTodos,
                        completionRate = completionRate,
                        topCategory = topCategory,
                        categoryDistribution = categoryDistribution,
                        trendData = trendData,
                        trendLabels = trendLabels,
                        currentStreak = currentStreak,
                        longestStreak = longestStreak,
                        productivityScore = productivityScore,
                        insight = insight
                    )
                }
            }
        }
    }
    
    private fun generateInsight(completionRate: Float, streak: Int, trend: List<Float>): String {
        return when {
            completionRate >= 0.8f && streak >= 3 -> "🔥 완벽해요! ${streak}일 연속 달성 중입니다!"
            completionRate >= 0.7f -> "👏 잘하고 있어요! 조금만 더 힘내세요!"
            trend.isNotEmpty() && trend.last() < completionRate -> "📈 이번 주 생산성이 증가했어요!"
            completionRate < 0.5f -> "💪 다시 시작해봐요! 작은 목표부터 도전하세요."
            else -> "✨ 꾸준히 실천하고 있어요. 계속 유지하세요!"
        }
    }

    fun onIntent(intent: StatisticsIntent) {
        when (intent) {
            StatisticsIntent.Load -> loadStatistics()
            is StatisticsIntent.ChangePeriod -> {
                _state.update { it.copy(period = intent.period) }
                loadStatistics()
            }
        }
    }
}
