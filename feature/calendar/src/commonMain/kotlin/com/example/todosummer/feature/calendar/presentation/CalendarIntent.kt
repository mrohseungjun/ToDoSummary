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
    data class AddTodo(val date: LocalDate, val title: String, val priority: com.example.todosummer.core.domain.model.Priority, val category: String) : CalendarIntent
    data class ToggleTodoCompletion(val todoId: String) : CalendarIntent
    data class UpdateTodo(val todo: com.example.todosummer.core.domain.model.Todo) : CalendarIntent
    data class DeleteTodo(val todoId: String) : CalendarIntent
}
