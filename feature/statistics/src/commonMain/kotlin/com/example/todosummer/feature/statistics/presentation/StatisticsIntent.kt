package com.example.todosummer.feature.statistics.presentation

/**
 * 통계 화면의 사용자 인텐트
 */
sealed interface StatisticsIntent {
    data object Load : StatisticsIntent
    data class ChangePeriod(val period: StatisticsPeriod) : StatisticsIntent
}
