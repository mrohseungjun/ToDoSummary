package com.example.todosummer.feature.todo.presentation

import com.example.todosummer.core.domain.model.Category
import com.example.todosummer.core.domain.model.Todo
import kotlinx.datetime.LocalDate

/**
 * 정렬 기준
 */
enum class SortType {
    CREATED_AT,   // 생성일순
    DUE_DATE,     // 마감일순
    PRIORITY,     // 우선순위순
    TITLE         // 제목순
}

/**
 * 필터 기준
 */
enum class FilterType {
    ALL,          // 전체
    COMPLETED,    // 완료된 항목만
    INCOMPLETE    // 미완료 항목만
}

// UI 상태: 불변 데이터 클래스 + StateFlow로 노출
// unstable mutable 컬렉션 금지: List<Todo> / Set<String> 사용
data class TodoState(
    val isLoading: Boolean = true,
    val todos: List<Todo> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedDate: LocalDate? = null,  // 선택된 날짜 (null이면 전체 보기)
    val error: String? = null,
    val sortType: SortType = SortType.CREATED_AT,
    val filterType: FilterType = FilterType.ALL,
    val filterCategory: String? = null,  // 카테고리 필터 (null이면 전체)
    val searchQuery: String = "",       // 검색어
    val selectedIds: Set<String> = emptySet() // 일괄 작업용 선택된 Todo ID 집합
)
