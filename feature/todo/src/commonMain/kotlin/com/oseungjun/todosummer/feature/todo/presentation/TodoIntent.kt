package com.oseungjun.todosummer.feature.todo.presentation

import com.oseungjun.todosummer.core.domain.model.Category
import com.oseungjun.todosummer.core.domain.model.Priority
import com.oseungjun.todosummer.core.domain.model.Todo
import kotlinx.datetime.LocalDate

sealed interface TodoIntent {
    data object Load : TodoIntent

    data class Add(
        val title: String,
        val priority: Priority,
        val category: String = "업무"
    ) : TodoIntent
    
    data class AddWithDetails(val todo: Todo) : TodoIntent

    data class Update(val todo: Todo) : TodoIntent
    data class Delete(val id: String) : TodoIntent
    data class Toggle(val id: String) : TodoIntent
    
    // 카테고리 관리
    data class AddCategory(val name: String) : TodoIntent
    data class DeleteCategory(val category: Category) : TodoIntent
    
    // 날짜 네비게이션
    data class SelectDate(val date: LocalDate?) : TodoIntent
    data object NavigateToPreviousDate : TodoIntent
    data object NavigateToNextDate : TodoIntent
    data object NavigateToToday : TodoIntent
    
    // 정렬/필터
    data class SetSortType(val sortType: SortType) : TodoIntent
    data class SetFilterType(val filterType: FilterType) : TodoIntent
    data class SetFilterCategory(val category: String?) : TodoIntent
    data class UpdateSearchQuery(val query: String) : TodoIntent

    // 멀티 선택
    data class ToggleSelection(val id: String) : TodoIntent
    data object ClearSelection : TodoIntent
    data object CompleteSelected : TodoIntent
    data object DeleteSelected : TodoIntent
}
