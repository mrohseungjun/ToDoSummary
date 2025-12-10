package com.oseungjun.todosummer.feature.calendar.presentation

import com.oseungjun.todosummer.core.domain.model.Category
import com.oseungjun.todosummer.core.domain.model.Todo
import kotlinx.datetime.LocalDate

/**
 * 캘린더 화면의 UI 상태
 */
data class CalendarState(
    val isLoading: Boolean = true,
    val todos: List<Todo> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedDate: LocalDate? = null,
    val currentMonth: YearMonth? = null,
    val error: String? = null
)

/**
 * YearMonth 헬퍼 클래스
 */
data class YearMonth(val year: Int, val monthNumber: Int) {
    fun plus(months: Int): YearMonth {
        var newMonth = monthNumber + months
        var newYear = year
        while (newMonth > 12) {
            newMonth -= 12
            newYear++
        }
        while (newMonth < 1) {
            newMonth += 12
            newYear--
        }
        return YearMonth(newYear, newMonth)
    }
    
    fun minus(months: Int) = plus(-months)
}
