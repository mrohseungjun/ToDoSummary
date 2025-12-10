package com.oseungjun.todosummer.feature.statistics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oseungjun.todosummer.core.domain.ai.GeminiService
import com.oseungjun.todosummer.core.domain.model.Todo
import com.oseungjun.todosummer.core.domain.usecase.TodoUseCases
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
    private val useCases: TodoUseCases,
    private val geminiService: GeminiService? = null
) : ViewModel() {
    
    // 현재 필터링된 Todo 목록 캐시
    private var currentFilteredTodos: List<Todo> = emptyList()
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
                }
                
                // 캐시 저장
                currentFilteredTodos = filteredTodos
                
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
            StatisticsIntent.GenerateAIReport -> generateAIReport()
            StatisticsIntent.AnalyzeProcrastination -> analyzeProcrastination()
            StatisticsIntent.ClearAIReport -> clearAIReport()
        }
    }
    
    /**
     * AI 사용 횟수 체크 및 증가
     */
    private fun checkAndIncrementAIUsage(): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today = "${now.date.year}-${now.date.monthNumber.toString().padStart(2, '0')}-${now.date.dayOfMonth.toString().padStart(2, '0')}"
        
        val currentState = _state.value
        
        // 날짜가 바뀌면 카운트 리셋
        val (newCount, newDate) = if (currentState.lastAIUsageDate != today) {
            1 to today
        } else {
            (currentState.dailyAIUsageCount + 1) to today
        }
        
        // 제한 초과 체크
        if (newCount > currentState.maxDailyAIUsage) {
            return false
        }
        
        _state.update { 
            it.copy(
                dailyAIUsageCount = newCount,
                lastAIUsageDate = newDate
            )
        }
        return true
    }
    
    /**
     * AI 리포트 생성
     */
    private fun generateAIReport() {
        if (geminiService == null) {
            _state.update { 
                it.copy(aiReportError = "AI 서비스가 설정되지 않았습니다. API 키를 확인해주세요.")
            }
            return
        }
        
        if (currentFilteredTodos.isEmpty()) {
            _state.update { 
                it.copy(aiReportError = "분석할 데이터가 없습니다.")
            }
            return
        }
        
        // 일일 사용 횟수 체크
        if (!checkAndIncrementAIUsage()) {
            _state.update { 
                it.copy(aiReportError = "오늘의 AI 분석 횟수(${_state.value.maxDailyAIUsage}회)를 모두 사용했습니다.")
            }
            return
        }
        
        viewModelScope.launch {
            _state.update { 
                it.copy(
                    isGeneratingAIReport = true,
                    aiReportError = null
                )
            }
            
            val periodLabel = when (_state.value.period) {
                StatisticsPeriod.WEEK -> "주간 (최근 7일)"
                StatisticsPeriod.MONTH -> "월간 (최근 30일)"
            }
            
            geminiService.generateReport(currentFilteredTodos, periodLabel)
                .onSuccess { report ->
                    _state.update {
                        it.copy(
                            isGeneratingAIReport = false,
                            aiReportSummary = report.summary,
                            aiReportInsights = report.insights,
                            aiReportActionItems = report.actionItems,
                            aiReportError = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isGeneratingAIReport = false,
                            aiReportError = "AI 리포트 생성 실패: ${error.message}"
                        )
                    }
                }
        }
    }
    
    /**
     * 미루기 패턴 분석
     */
    private fun analyzeProcrastination() {
        if (geminiService == null) {
            _state.update { 
                it.copy(procrastinationError = "AI 서비스가 설정되지 않았습니다. API 키를 확인해주세요.")
            }
            return
        }
        
        if (currentFilteredTodos.isEmpty()) {
            _state.update { 
                it.copy(procrastinationError = "분석할 데이터가 없습니다.")
            }
            return
        }
        
        // 일일 사용 횟수 체크
        if (!checkAndIncrementAIUsage()) {
            _state.update { 
                it.copy(procrastinationError = "오늘의 AI 분석 횟수(${_state.value.maxDailyAIUsage}회)를 모두 사용했습니다.")
            }
            return
        }
        
        viewModelScope.launch {
            _state.update { 
                it.copy(
                    isAnalyzingProcrastination = true,
                    procrastinationError = null
                )
            }
            
            geminiService.analyzeProcrastination(currentFilteredTodos)
                .onSuccess { patterns ->
                    _state.update {
                        it.copy(
                            isAnalyzingProcrastination = false,
                            procrastinationCategories = patterns.frequentCategories,
                            procrastinationTimeSlots = patterns.frequentTimeSlots,
                            procrastinationComment = patterns.aiComment,
                            procrastinationError = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isAnalyzingProcrastination = false,
                            procrastinationError = "미루기 패턴 분석 실패: ${error.message}"
                        )
                    }
                }
        }
    }
    
    /**
     * AI 리포트 초기화
     */
    private fun clearAIReport() {
        _state.update {
            it.copy(
                aiReportSummary = "",
                aiReportInsights = emptyList(),
                aiReportActionItems = emptyList(),
                aiReportError = null,
                procrastinationCategories = emptyList(),
                procrastinationTimeSlots = emptyList(),
                procrastinationComment = "",
                procrastinationError = null
            )
        }
    }
}
