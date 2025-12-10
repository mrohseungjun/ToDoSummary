package com.oseungjun.todosummer.feature.calendar.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oseungjun.todosummer.core.domain.model.Todo
import com.oseungjun.todosummer.core.ui.toast.rememberToastState
import com.oseungjun.todosummer.feature.todo.presentation.components.TodoEditScreen
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    state: CalendarState,
    onIntent: (CalendarIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    // Toast 상태
    val toastState = rememberToastState()

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val currentMonth = state.currentMonth ?: YearMonth(today.year, today.monthNumber)
    val selectedDate = state.selectedDate

    // 선택된 날짜의 Todo 목록
    val todosForSelectedDate = remember(selectedDate, state.todos) {
        selectedDate?.let { date ->
            state.todos.filter { it.createdAt.date == date || it.dueDate?.date == date }
        } ?: emptyList()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 월 헤더
            MonthHeader(
                currentMonth = currentMonth,
                onPreviousMonth = { onIntent(CalendarIntent.NavigateToPreviousMonth) },
                onNextMonth = { onIntent(CalendarIntent.NavigateToNextMonth) },
                onTodayClick = { onIntent(CalendarIntent.NavigateToToday) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 요일 헤더
            WeekdayHeader()

            Spacer(modifier = Modifier.height(8.dp))

            // 캘린더 그리드
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                today = today,
                todos = state.todos,
                onDateClick = { date ->
                    onIntent(CalendarIntent.SelectDate(date))
                    showBottomSheet = true
                }
            )
        }
    }

    // 수정할 Todo 상태
    var editingTodo by remember { mutableStateOf<Todo?>(null) }
    // 삭제 확인 다이얼로그
    var showDeleteDialog by remember { mutableStateOf(false) }
    var todoToDelete by remember { mutableStateOf<Todo?>(null) }

    // 바텀시트: 선택된 날짜의 Todo 목록 + 추가 기능
    if (showBottomSheet && selectedDate != null) {
        TodoBottomSheet(
            selectedDate = selectedDate,
            todos = todosForSelectedDate,
            onDismiss = { showBottomSheet = false },
            onAddTodo = { showAddDialog = true },
            onToggleCompletion = { todoId -> onIntent(CalendarIntent.ToggleTodoCompletion(todoId)) },
            onTodoClick = { todo -> editingTodo = todo },
            onDeleteTodo = { todo ->
                todoToDelete = todo
                showDeleteDialog = true
            }
        )
    }
    
    // 삭제 확인 다이얼로그
    if (showDeleteDialog && todoToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { 
                Text(
                    text = "정말로 삭제하시겠습니까?",
                    style = MaterialTheme.typography.titleLarge
                ) 
            },
            text = { 
                Text(
                    text = "\'${todoToDelete?.title}\' 항목이 삭제됩니다. 이 작업은 되돌릴 수 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        todoToDelete?.let { onIntent(CalendarIntent.DeleteTodo(it.id)) }
                        showDeleteDialog = false
                        todoToDelete = null
                        toastState.show("삭제되었습니다")
                    }
                ) {
                    Text(
                        text = "삭제",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteDialog = false
                    todoToDelete = null
                }) {
                    Text("취소")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Todo 수정 다이얼로그
    editingTodo?.let { todo ->
        TodoEditScreen(
            todo = todo,
            categories = state.categories.map { it.name },
            onSave = { updatedTodo ->
                onIntent(CalendarIntent.UpdateTodo(updatedTodo))
                editingTodo = null
                toastState.show("수정되었습니다")
            },
            onAddCategory = { categoryName -> onIntent(CalendarIntent.AddCategory(categoryName)) },
            onDeleteCategory = { categoryName -> onIntent(CalendarIntent.DeleteCategory(categoryName)) },
            onCancel = { editingTodo = null }
        )
    }

    // Todo 추가 다이얼로그 (TodoEditScreen 재사용)
    if (showAddDialog && selectedDate != null) {
        TodoEditScreen(
            todo = null,
            categories = state.categories.map { it.name },
            onSave = { newTodo ->
                // 선택된 날짜로 createdAt 설정
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val todoWithDate = newTodo.copy(
                    createdAt = LocalDateTime(selectedDate, now.time)
                )
                onIntent(CalendarIntent.AddTodo(
                    selectedDate,
                    todoWithDate.title,
                    todoWithDate.priority,
                    todoWithDate.category
                ))
                showAddDialog = false
                toastState.show("추가되었습니다")
            },
            onAddCategory = { categoryName -> onIntent(CalendarIntent.AddCategory(categoryName)) },
            onDeleteCategory = { categoryName -> onIntent(CalendarIntent.DeleteCategory(categoryName)) },
            onCancel = { showAddDialog = false }
        )
    }
}

@Composable
private fun MonthHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${currentMonth.year}년 ${currentMonth.monthNumber}월",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "이전 달"
                )
            }
            
            TextButton(onClick = onTodayClick) {
                Text("오늘", color = Color(0xFF5E7CE2))
            }
            
            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "다음 달"
                )
            }
        }
    }
}

@Composable
private fun WeekdayHeader() {
    val weekdays = listOf("일", "월", "화", "수", "목", "금", "토")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        weekdays.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    today: LocalDate,
    todos: List<Todo>,
    onDateClick: (LocalDate) -> Unit
) {
    val firstDayOfMonth = LocalDate(currentMonth.year, currentMonth.monthNumber, 1)
    // 일요일을 0, 월요일을 1, ... 토요일을 6으로 변환
    val firstDayOfWeek = when (firstDayOfMonth.dayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        else -> 0
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        var currentDay = firstDayOfMonth.minus(firstDayOfWeek, DateTimeUnit.DAY)
        
        repeat(6) { weekIndex ->
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(7) { dayIndex ->
                    val date = currentDay
                    val isCurrentMonth = date.monthNumber == currentMonth.monthNumber
                    val isToday = date == today
                    val isSelected = date == selectedDate
                    val hasTodos = todos.any { 
                        it.createdAt.date == date || it.dueDate?.date == date 
                    }
                    
                    CalendarDay(
                        date = date,
                        isCurrentMonth = isCurrentMonth,
                        isToday = isToday,
                        isSelected = isSelected,
                        hasTodos = hasTodos,
                        onClick = { if (isCurrentMonth) onDateClick(date) },
                        modifier = Modifier.weight(1f)
                    )
                    
                    currentDay = currentDay.plus(1, DateTimeUnit.DAY)
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    hasTodos: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> Color(0xFF5E7CE2)
                    isToday -> Color(0xFF5E7CE2).copy(alpha = 0.3f)
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = isCurrentMonth) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    isSelected -> Color.White
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            
            if (hasTodos && isCurrentMonth) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color.White else Color(0xFF5E7CE2)
                        )
                )
            }
        }
    }
}

@Composable
private fun TodoItemInSheet(
    todo: Todo,
    onToggleCompletion: (String) -> Unit,
    onTodoClick: (Todo) -> Unit,
    onDeleteTodo: (Todo) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = Color(0xFFBBDEFB).copy(alpha = 0.3f))
            .clickable { onTodoClick(todo) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = todo.isCompleted,
            onCheckedChange = { onToggleCompletion(todo.id) }
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = todo.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (todo.isCompleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            // 카테고리 표시
            if (todo.category.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = todo.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            }
        }
        
        // 삭제 버튼
        IconButton(
            onClick = { onDeleteTodo(todo) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "삭제",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun getDayOfWeekKorean(dayOfWeek: DayOfWeek): String {
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> "월요일"
        DayOfWeek.TUESDAY -> "화요일"
        DayOfWeek.WEDNESDAY -> "수요일"
        DayOfWeek.THURSDAY -> "목요일"
        DayOfWeek.FRIDAY -> "금요일"
        DayOfWeek.SATURDAY -> "토요일"
        DayOfWeek.SUNDAY -> "일요일"
        else -> ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoBottomSheet(
    selectedDate: LocalDate,
    todos: List<Todo>,
    onDismiss: () -> Unit,
    onAddTodo: () -> Unit,
    onToggleCompletion: (String) -> Unit,
    onTodoClick: (Todo) -> Unit,
    onDeleteTodo: (Todo) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedDate.monthNumber}월 ${selectedDate.dayOfMonth}일 ${getDayOfWeekKorean(selectedDate.dayOfWeek)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onAddTodo) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "할 일 추가",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (todos.isEmpty()) {
                Text(
                    text = "이 날짜에 할 일이 없습니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    items(todos) { todo ->
                        TodoItemInSheet(
                            todo = todo,
                            onToggleCompletion = onToggleCompletion,
                            onTodoClick = onTodoClick,
                            onDeleteTodo = onDeleteTodo
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


