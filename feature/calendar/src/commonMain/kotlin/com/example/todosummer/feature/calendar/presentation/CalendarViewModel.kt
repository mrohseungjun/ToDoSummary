package com.example.todosummer.feature.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todosummer.core.domain.usecase.TodoUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * 캘린더 화면의 상태를 관리하는 ViewModel
 * Clean Architecture + MVI 패턴 준수
 */
class CalendarViewModel(
    private val useCases: TodoUseCases
) : ViewModel() {
    
    private val _state = MutableStateFlow(CalendarState())
    val state: StateFlow<CalendarState> = _state.asStateFlow()
    
    init {
        // 초기 로드
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        _state.update { 
            it.copy(
                currentMonth = YearMonth(now.year, now.monthNumber)
            )
        }
        loadTodos()
    }
    
    /**
     * Todo 목록 로드
     */
    private fun loadTodos() {
        viewModelScope.launch {
            try {
                useCases.getTodos().collect { todos ->
                    _state.update { it.copy(isLoading = false, todos = todos) }
                }
            } catch (e: Exception) {
                _state.update { 
                    it.copy(
                        isLoading = false, 
                        error = "Todo 로드 중 오류가 발생했습니다: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * 날짜 선택
     */
    fun selectDate(date: kotlinx.datetime.LocalDate) {
        _state.update { it.copy(selectedDate = date) }
    }
    
    /**
     * 월 변경
     */
    fun changeMonth(yearMonth: YearMonth) {
        _state.update { it.copy(currentMonth = yearMonth) }
    }
    
    /**
     * 이전 달로 이동
     */
    fun navigateToPreviousMonth() {
        _state.value.currentMonth?.let { current ->
            _state.update { it.copy(currentMonth = current.minus(1)) }
        }
    }
    
    /**
     * 다음 달로 이동
     */
    fun navigateToNextMonth() {
        _state.value.currentMonth?.let { current ->
            _state.update { it.copy(currentMonth = current.plus(1)) }
        }
    }
    
    /**
     * 오늘로 이동
     */
    fun navigateToToday() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        _state.update { 
            it.copy(
                currentMonth = YearMonth(now.year, now.monthNumber),
                selectedDate = now.date
            )
        }
    }
    
    /**
     * 인텐트 기반 액션 처리 (MVI 패턴)
     */
    fun onIntent(intent: CalendarIntent) {
        when (intent) {
            CalendarIntent.Load -> loadTodos()
            is CalendarIntent.SelectDate -> selectDate(intent.date)
            is CalendarIntent.ChangeMonth -> changeMonth(intent.yearMonth)
            CalendarIntent.NavigateToPreviousMonth -> navigateToPreviousMonth()
            CalendarIntent.NavigateToNextMonth -> navigateToNextMonth()
            CalendarIntent.NavigateToToday -> navigateToToday()
        }
    }
}
