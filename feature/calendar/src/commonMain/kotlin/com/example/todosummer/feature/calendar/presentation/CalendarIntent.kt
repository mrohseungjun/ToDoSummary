package com.example.todosummer.feature.calendar.presentation

import kotlinx.datetime.LocalDate

/**
 * 캘린더 화면의 사용자 인텐트
 */
sealed interface CalendarIntent {
    data object Load : CalendarIntent
    data class SelectDate(val date: LocalDate) : CalendarIntent
    data class ChangeMonth(val yearMonth: YearMonth) : CalendarIntent
    data object NavigateToPreviousMonth : CalendarIntent
    data object NavigateToNextMonth : CalendarIntent
    data object NavigateToToday : CalendarIntent
}
