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
import androidx.compose.ui.unit.sp
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.ui.AppIcons
import com.example.todosummer.core.ui.components.AppFab
import com.example.todosummer.core.ui.theme.Dimens
import com.example.todosummer.feature.todo.presentation.components.TodoEditScreen
import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.feature.todo.presentation.components.TodoItem
import kotlinx.coroutines.flow.MutableStateFlow
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

@Preview
@Composable
fun TodoListRoutePreview() {
    TodoListRoute(onOpenStatistics = {})
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 날짜 네비게이션 바
            DateNavigationBar(
                dateText = "${today.monthNumber}월 ${today.dayOfMonth}일 ${getDayOfWeekKorean(today.dayOfWeek.name)}",
                onPreviousDay = { /* TODO */ },
                onNextDay = { /* TODO */ }
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (state.todos.isEmpty()) {
                    EmptyState(
                        messageTitle = strings.todoEmptyTitle,
                        messageBody = strings.todoEmptyBody
                    )
                } else {
                    TodoList(
                        todos = state.todos,
                        onToggleCompletion = { viewModel.toggleTodoCompletion(it.id) },
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
            onSave = { todo ->
                if (currentTodo == null) {
                    viewModel.addTodo(
                        title = todo.title,
                        description = todo.description,
                        priority = todo.priority,
                        dueDate = todo.dueDate
                    )
                } else {
                    viewModel.updateTodo(todo)
                }
                showAddEditDialog = false
            },
            onCancel = { showAddEditDialog = false }
        )
    }

    // 삭제 확인 다이얼로그
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(strings.deleteTodo) },
            text = { Text("${strings.deleteTodo}: ${currentTodo?.title}") },
            confirmButton = {
                TextButton(
                    onClick = {
                        currentTodo?.let { viewModel.deleteTodo(it.id) }
                        showDeleteDialog = false
                    }
                ) {
                    Text(strings.delete)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(strings.cancel)
                }
            }
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
        Todo(id = "1", title = "Buy groceries", description = null, isCompleted = false, createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), updatedAt = null, dueDate = null, priority = Priority.MEDIUM),
        Todo(id = "2", title = "Walk the dog", description = "Morning walk", isCompleted = true, createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), updatedAt = null, dueDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), priority = Priority.LOW),
        Todo(id = "3", title = "Read a book", description = "Chapter 5", isCompleted = false, createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), updatedAt = null, dueDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), priority = Priority.HIGH)
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
                todo.dueDate?.let { dueDate ->
                    Spacer(modifier = Modifier.height(Dimens.spacing4))
                    Text(
                        text = "${dueDate.hour}:${dueDate.minute.toString().padStart(2, '0')} ${if (dueDate.hour < 12) "AM" else "PM"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }
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
    val todo = Todo(id = "1", title = "Sample Todo", description = "This is a sample todo item.", isCompleted = false, createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), updatedAt = null, dueDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()), priority = Priority.MEDIUM)
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
