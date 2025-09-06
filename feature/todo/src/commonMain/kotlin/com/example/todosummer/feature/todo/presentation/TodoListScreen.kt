package com.example.todosummer.feature.todo.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.domain.usecase.TodoUseCases
import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.ui.AppIcons
import com.example.todosummer.core.ui.components.AppFab
import com.example.todosummer.core.ui.components.AppTopBar
import com.example.todosummer.core.ui.theme.Dimens
import kotlinx.datetime.LocalDateTime
import com.example.todosummer.feature.todo.presentation.components.TodoEditScreen
import com.example.todosummer.feature.todo.presentation.components.TodoItem
import org.koin.compose.koinInject
 import androidx.lifecycle.viewmodel.compose.viewModel
 import androidx.lifecycle.viewmodel.viewModelFactory
 import androidx.lifecycle.viewmodel.initializer
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Todo 목록을 표시하는 화면
 */
@Composable
fun TodoListRoute(
    onOpenAISummary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val useCases: TodoUseCases = koinInject()
    val viewModel: TodoViewModel = viewModel(
        factory = viewModelFactory {
            initializer { TodoViewModel(useCases) }
        }
    )
    TodoListScreen(viewModel = viewModel, onOpenAISummary = onOpenAISummary, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onOpenAISummary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val state by viewModel.state.collectAsState()
    
    var showAddEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentTodo by remember { mutableStateOf<Todo?>(null) }
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    
    Scaffold(
        topBar = {
            AppTopBar(title = strings.todos)
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = Dimens.size64)
            ) {
                AppFab(
                    icon = AppIcons.Add,
                    contentDescription = strings.addTodo,
                    onClick = {
                        currentTodo = null
                        showAddEditDialog = true
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.spacing16)
                ) {
                    // 헤더: 오늘 할 일 + 날짜
                    Text(
                        text = "오늘 할 일",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(top = Dimens.spacing8)
                    )
                    Text(
                        text = "${today.year}년 ${today.monthNumber}월 ${today.dayOfMonth}일",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Dimens.spacing4, bottom = Dimens.spacing12)
                    )

                    // 요약 보기 카드
                    SummaryTile(
                        title = strings.aiSummaryTitle,
                        description = strings.aiSummarize,
                        onClick = onOpenAISummary
                    )

                    Spacer(modifier = Modifier.height(Dimens.spacing12))

                    if (state.todos.isEmpty()) {
                        // 비어있을 때 안내
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            androidx.compose.material3.Icon(
                                imageVector = AppIcons.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                modifier = Modifier.size(Dimens.size64)
                            )
                            Spacer(modifier = Modifier.height(Dimens.spacing12))
                            Text(
                                text = strings.todoEmpty,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    } else {
                        // 할 일 목록
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
                            contentPadding = PaddingValues(0.dp)
                        )
                    }
                }
            }
            
            // 에러 메시지 표시
            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(Dimens.spacing16)
                )
            }
        }
    }
    
    // 할 일 추가/편집 다이얼로그
    AnimatedVisibility(
        visible = showAddEditDialog,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it }
    ) {
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
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding
    ) {
        items(items = todos, key = { it.id }) { todo ->
            TodoItem(
                todo = todo,
                onToggleCompletion = { onToggleCompletion(todo) },
                onEdit = { onEdit(todo) },
                onDelete = { onDelete(todo) }
            )
            
            Spacer(modifier = Modifier.height(Dimens.spacing8))
        }
    }
}

@Composable
private fun SummaryTile(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.Card(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(Dimens.radius8),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = Dimens.elevation2),
        colors = androidx.compose.material3.CardDefaults.cardColors()
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacing12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🤖", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(Dimens.spacing12))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(Dimens.spacing4))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
