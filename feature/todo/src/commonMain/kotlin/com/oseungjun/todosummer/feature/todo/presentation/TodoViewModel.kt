package com.oseungjun.todosummer.feature.todo.presentation

import com.oseungjun.todosummer.core.domain.model.Category
import com.oseungjun.todosummer.core.domain.model.Priority
import com.oseungjun.todosummer.core.domain.model.Todo
import com.oseungjun.todosummer.core.domain.repository.CategoryRepository
import com.oseungjun.todosummer.core.domain.usecase.TodoUseCases
import com.oseungjun.todosummer.core.domain.notification.NotificationScheduler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.random.Random

/**
 * Todo 화면의 상태를 관리하는 ViewModel
 */
class TodoViewModel(
    private val useCases: TodoUseCases,
    private val categoryRepository: CategoryRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    private val _state = MutableStateFlow(TodoState())
    val state: StateFlow<TodoState> = _state.asStateFlow()
    
    init {
        // 초기 로드: 리포지토리 Flow 수집
        viewModelScope.launch {
            useCases.getTodos().collect { todos ->
                _state.update { it.copy(isLoading = false, todos = todos) }
            }
        }
        
        // 카테고리 Flow 수집
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
    }
    
    /**
     * 새로운 Todo 항목을 추가합니다.
     */
    fun addTodo(title: String, priority: Priority, category: String = "업무") {
        if (title.isBlank()) return
        
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        // 선택된 날짜가 있으면 그 날짜로 생성, 없으면 오늘
        val createdAt = _state.value.selectedDate?.let { selectedDate ->
            // 선택된 날짜 + 현재 시간
            LocalDateTime(selectedDate, now.time)
        } ?: now

        val todo = Todo(
            id = "",
            title = title,
            isCompleted = false,
            createdAt = createdAt,
            updatedAt = null,
            dueDate = null,
            priority = priority,
            category = category
        )
        
        viewModelScope.launch {
            useCases.addTodo(todo)
        }
    }
    
    /**
     * 상세 정보(알림 포함)가 있는 Todo 항목을 추가합니다.
     */
    fun addTodoWithDetails(todo: Todo) {
        if (todo.title.isBlank()) return
        
        viewModelScope.launch {
            try {
                useCases.addTodo(todo)
                
                // 알림 스케줄링
                if (todo.hasReminder && todo.reminderTime != null) {
                    notificationScheduler.scheduleNotification(todo)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Todo 추가 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }
    
    /**
     * Todo 항목을 삭제합니다.
     */
    fun deleteTodo(id: String) {
        viewModelScope.launch {
            try {
                // 알림 취소
                notificationScheduler.cancelNotification(id)
                useCases.deleteTodo(id)
            } catch (e: Exception) {
                _state.update { it.copy(error = "삭제 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }
    
    /**
     * Todo 항목의 완료 상태를 토글합니다.
     */
    fun toggleTodoCompletion(id: String) {
        viewModelScope.launch {
            try {
                useCases.toggleTodoCompletion(id)
            } catch (e: Exception) {
                _state.update { it.copy(error = "상태 변경 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }
    
    /**
     * Todo 항목을 업데이트합니다.
     */
    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                val updatedTodo = todo.copy(
                    updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
                useCases.updateTodo(updatedTodo)
                
                // 알림 스케줄링 (기존 알림 취소 후 새로 설정)
                notificationScheduler.cancelNotification(updatedTodo.id)
                if (updatedTodo.hasReminder && updatedTodo.reminderTime != null) {
                    notificationScheduler.scheduleNotification(updatedTodo)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "업데이트 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }

    /**
     * 새 카테고리를 추가합니다 (최대 10개)
     */
    fun addCategory(name: String) {
        if (name.isBlank()) return
        
        viewModelScope.launch {
            try {
                val currentCategories = _state.value.categories
                
                // 최대 10개 제한
                if (currentCategories.size >= 10) {
                    _state.update { it.copy(error = "카테고리는 최대 10개까지만 추가할 수 있습니다.") }
                    return@launch
                }
                
                // 중복 체크
                if (currentCategories.any { it.name == name }) {
                    _state.update { it.copy(error = "이미 존재하는 카테고리입니다.") }
                    return@launch
                }
                
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val category = Category(
                    id = "category_${Random.nextLong()}",
                    name = name,
                    createdAt = now
                )
                categoryRepository.addCategory(category)
            } catch (e: Exception) {
                _state.update { it.copy(error = "카테고리 추가 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }
    
    /**
     * 카테고리를 삭제합니다
     */
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(category)
            } catch (e: Exception) {
                _state.update { it.copy(error = "카테고리 삭제 중 오류가 발생했습니다: ${e.message}") }
            }
        }
    }
    
    /**
     * 날짜를 선택합니다
     */
    fun selectDate(date: LocalDate?) {
        _state.update { it.copy(selectedDate = date) }
    }
    
    /**
     * 이전 날짜로 이동
     */
    fun navigateToPreviousDate() {
        val currentDate = _state.value.selectedDate
            ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val previousDate = currentDate.minus(DatePeriod(days = 1))
        _state.update { it.copy(selectedDate = previousDate) }
    }
    
    /**
     * 다음 날짜로 이동
     */
    fun navigateToNextDate() {
        val currentDate = _state.value.selectedDate
            ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val nextDate = currentDate.plus(DatePeriod(days = 1))
        _state.update { it.copy(selectedDate = nextDate) }
    }
    
    /**
     * 오늘 날짜로 이동
     */
    fun navigateToToday() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        _state.update { it.copy(selectedDate = today) }
    }
    
    /**
     * 정렬 기준 변경
     */
    fun setSortType(sortType: SortType) {
        _state.update { it.copy(sortType = sortType) }
    }
    
    /**
     * 필터 기준 변경
     */
    fun setFilterType(filterType: FilterType) {
        _state.update { it.copy(filterType = filterType) }
    }
    
    /**
     * 카테고리 필터 변경
     */
    fun setFilterCategory(category: String?) {
        _state.update { it.copy(filterCategory = category) }
    }

    /**
     * 인텐트 기반 액션 처리
     */
    fun onIntent(intent: TodoIntent) {
        when (intent) {
            is TodoIntent.Add -> addTodo(
                title = intent.title,
                priority = intent.priority,
                category = intent.category
            )
            is TodoIntent.AddWithDetails -> addTodoWithDetails(intent.todo)
            is TodoIntent.Update -> updateTodo(intent.todo)
            is TodoIntent.Delete -> deleteTodo(intent.id)
            is TodoIntent.Toggle -> toggleTodoCompletion(intent.id)
            is TodoIntent.AddCategory -> addCategory(intent.name)
            is TodoIntent.DeleteCategory -> deleteCategory(intent.category)
            is TodoIntent.SelectDate -> selectDate(intent.date)
            TodoIntent.NavigateToPreviousDate -> navigateToPreviousDate()
            TodoIntent.NavigateToNextDate -> navigateToNextDate()
            TodoIntent.NavigateToToday -> navigateToToday()
            TodoIntent.Load -> { /* 초기 수집으로 대체됨 */ }
            is TodoIntent.SetSortType -> setSortType(intent.sortType)
            is TodoIntent.SetFilterType -> setFilterType(intent.filterType)
            is TodoIntent.SetFilterCategory -> setFilterCategory(intent.category)

            // 검색어 변경
            is TodoIntent.UpdateSearchQuery -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }

            // 멀티 선택 토글
            is TodoIntent.ToggleSelection -> {
                _state.update { state ->
                    val current = state.selectedIds
                    val newSet = if (current.contains(intent.id)) current - intent.id else current + intent.id
                    state.copy(selectedIds = newSet)
                }
            }

            // 선택 해제
            TodoIntent.ClearSelection -> {
                _state.update { it.copy(selectedIds = emptySet()) }
            }

            // 선택 항목 일괄 완료
            TodoIntent.CompleteSelected -> {
                val ids = _state.value.selectedIds
                if (ids.isNotEmpty()) {
                    viewModelScope.launch {
                        try {
                            val currentTodos = _state.value.todos
                            ids.forEach { id ->
                                val todo = currentTodos.find { it.id == id } ?: return@forEach
                                if (!todo.isCompleted) {
                                    useCases.toggleTodoCompletion(id)
                                }
                            }
                            _state.update { it.copy(selectedIds = emptySet()) }
                        } catch (e: Exception) {
                            _state.update { it.copy(error = "일괄 완료 중 오류가 발생했습니다: ${e.message}") }
                        }
                    }
                }
            }

            // 선택 항목 일괄 삭제
            TodoIntent.DeleteSelected -> {
                val ids = _state.value.selectedIds
                if (ids.isNotEmpty()) {
                    viewModelScope.launch {
                        try {
                            ids.forEach { id ->
                                notificationScheduler.cancelNotification(id)
                                useCases.deleteTodo(id)
                            }
                            _state.update { it.copy(selectedIds = emptySet()) }
                        } catch (e: Exception) {
                            _state.update { it.copy(error = "일괄 삭제 중 오류가 발생했습니다: ${e.message}") }
                        }
                    }
                }
            }
        }
    }
}

