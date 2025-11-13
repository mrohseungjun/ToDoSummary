package com.example.todosummer.feature.todo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.ui.AppIcons
import com.example.todosummer.core.ui.components.AppFab
import com.example.todosummer.core.ui.theme.Dimens
import com.example.todosummer.feature.todo.presentation.components.TodoEditScreen
import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.feature.todo.presentation.components.TodoItem
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

/**
 * Todo 목록을 표시하는 화면
 */
@Composable
fun TodoListRoute(
    onOpenStatistics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TodoViewModel = koinViewModel()
    TodoListScreen(viewModel = viewModel, onOpenStatistics = onOpenStatistics, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onOpenStatistics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val state by viewModel.state.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentTodo by remember { mutableStateOf<Todo?>(null) }
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    
    // 선택된 날짜 (null이면 오늘)
    val selectedDate = state.selectedDate ?: today.date
    
    // 날짜별 필터링된 Todo 목록 (마감일 고려)
    val filteredTodos = remember(state.todos, selectedDate, today.date) {
        state.todos.filter { todo ->
            val todoDate = todo.createdAt.date
            val todoDueDate = todo.dueDate?.date
            val isToday = selectedDate == today.date
            val isPast = selectedDate < today.date
            
            when {
                // 오늘: 오늘 생성된 Todo + 과거에 생성된 미완료 Todo + 오늘이 마감일인 Todo
                isToday -> {
                    (todoDate == selectedDate) || 
                    (todoDate < selectedDate && !todo.isCompleted) ||
                    (todoDueDate == selectedDate)
                }
                
                // 과거: 해당 날짜에 생성된 Todo (완료 여부 무관)
                isPast -> {
                    todoDate == selectedDate
                }
                
                // 미래: 해당 날짜에 생성되거나 마감일인 Todo
                else -> {
                    todoDate == selectedDate || todoDueDate == selectedDate
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 날짜 네비게이션 바
            DateNavigationBar(
                dateText = "${selectedDate.monthNumber}월 ${selectedDate.dayOfMonth}일",
                onPreviousDay = { viewModel.onIntent(TodoIntent.NavigateToPreviousDate) },
                onNextDay = { viewModel.onIntent(TodoIntent.NavigateToNextDate) }
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (filteredTodos.isEmpty()) {
                    EmptyState(
                        messageTitle = "할 일이 없습니다",
                        messageBody = "새로운 할 일을 추가해보세요"
                    )
                } else {
                    TodoList(
                        todos = filteredTodos,
                        onToggleCompletion = { viewModel.onIntent(TodoIntent.Toggle(it.id)) },
                        onEdit = {
                            currentTodo = it
                            showAddEditDialog = true
                        },
                        onDelete = {
                            currentTodo = it
                            showDeleteDialog = true
                        },
                        onOpenStatistics = onOpenStatistics
                    )
                }
            }
        }

        // FAB
        AppFab(
            icon = AppIcons.Add,
            contentDescription = strings.addTodo,
            onClick = {
                currentTodo = null
                showAddEditDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Dimens.spacing16)
                .navigationBarsPadding()
        )
    }

    // 할 일 추가/편집: 바텀 시트 표시
    if (showAddEditDialog) {
        TodoEditScreen(
            todo = currentTodo,
            categories = state.categories.map { it.name },
            onSave = { todo ->
                if (currentTodo == null) {
                    // 새 Todo 추가 - 전체 Todo 객체를 사용하여 알림 정보 포함
                    viewModel.onIntent(TodoIntent.AddWithDetails(todo))
                } else {
                    viewModel.onIntent(TodoIntent.Update(todo))
                }
                showAddEditDialog = false
            },
            onAddCategory = { categoryName ->
                viewModel.onIntent(TodoIntent.AddCategory(categoryName))
            },
            onDeleteCategory = { categoryName ->
                state.categories.find { it.name == categoryName }?.let { category ->
                    viewModel.onIntent(TodoIntent.DeleteCategory(category))
                }
            },
            onCancel = { showAddEditDialog = false }
        )
    }

    // 삭제 확인 다이얼로그
    if (showDeleteDialog) {
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
                    text = "이 작업은 되돌릴 수 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        currentTodo?.let { viewModel.onIntent(TodoIntent.Delete(it.id)) }
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        text = "삭제",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

/**
 * Todo 목록을 표시하는 LazyColumn
 */
@Composable
fun TodoList(
    todos: List<Todo>,
    onToggleCompletion: (Todo) -> Unit,
    onEdit: (Todo) -> Unit,
    onDelete: (Todo) -> Unit,
    onOpenStatistics: () -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            top = Dimens.spacing16,
            start = Dimens.spacing16,
            end = Dimens.spacing16,
            bottom = Dimens.spacing16
        )
    ) {
        items(items = todos, key = { it.id }) { todo ->
            TodoItem(
                todo = todo,
                onToggleCompletion = { onToggleCompletion(todo) },
                onEdit = { onEdit(todo) },
                onDelete = { onDelete(todo) },
                modifier = Modifier.padding(bottom = Dimens.spacing12)
            )
        }
    }
}

@Preview
@Composable
fun TodoListPreview() {
    val todos = listOf(
        Todo(id = "1", title = "프로젝트 기획서 작성", isCompleted = false, createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), updatedAt = null, dueDate = null, priority = Priority.HIGH, category = "업무"),
        Todo(id = "2", title = "운동하기", isCompleted = true, createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), updatedAt = null, dueDate = null, priority = Priority.MEDIUM, category = "운동"),
        Todo(id = "3", title = "책 읽기", isCompleted = false, createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), updatedAt = null, dueDate = null, priority = Priority.LOW, category = "개인")
    )
    TodoList(
        todos = todos,
        onToggleCompletion = {},
        onEdit = {},
        onDelete = {}
    )
}

@Composable
private fun DateNavigationBar(
    dateText: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous day",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = dateText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onNextDay) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next day",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun DateNavigationBarPreview() {
    DateNavigationBar(
        dateText = "7월 25일 목요일",
        onPreviousDay = {},
        onNextDay = {}
    )
}

fun getDayOfWeekKorean(dayOfWeek: String): String {
    return when (dayOfWeek.uppercase()) {
        "MONDAY" -> "월요일"
        "TUESDAY" -> "화요일"
        "WEDNESDAY" -> "수요일"
        "THURSDAY" -> "목요일"
        "FRIDAY" -> "금요일"
        "SATURDAY" -> "토요일"
        "SUNDAY" -> "일요일"
        else -> dayOfWeek
    }
}

@Composable
private fun TodoCard(
    todo: Todo,
    onToggleCompletion: (Todo) -> Unit,
    onEdit: (Todo) -> Unit,
    onDelete: (Todo) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggleCompletion(todo) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Column {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (todo.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (todo.isCompleted)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None
                )
                Spacer(modifier = Modifier.height(Dimens.spacing4))
                Text(
                    text = todo.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        IconButton(onClick = { onEdit(todo) }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun TodoCardPreview() {
    val todo = Todo(
        id = "1", 
        title = "프로젝트 기획서 작성", 
        isCompleted = false, 
        createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        updatedAt = null,
        dueDate = null,
        priority = Priority.HIGH,
        category = "업무"
    )
    TodoCard(
        todo = todo,
        onToggleCompletion = {},
        onEdit = {},
        onDelete = {}
    )
}

@Composable
private fun EmptyState(
    messageTitle: String,
    messageBody: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = AppIcons.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(Dimens.spacing24))
        Text(
            text = messageTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimens.spacing8))
        Text(
            text = messageBody,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun EmptyStatePreview() {
    EmptyState(
        messageTitle = "No Todos",
        messageBody = "You have no tasks for today. Add one!"
    )
}

@Preview
@Composable
fun TodoListRoutePreview() {
    TodoListRoute(onOpenStatistics = {})
}
