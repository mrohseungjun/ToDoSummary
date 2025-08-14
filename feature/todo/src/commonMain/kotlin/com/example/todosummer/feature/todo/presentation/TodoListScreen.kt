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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import kotlinx.datetime.LocalDateTime
import com.example.todosummer.feature.todo.presentation.components.TodoEditScreen
import com.example.todosummer.feature.todo.presentation.components.TodoItem

/**
 * Todo 목록을 표시하는 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val state by viewModel.state.collectAsState()
    
    var showAddEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var currentTodo by remember { mutableStateOf<Todo?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.todos) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    currentTodo = null
                    showAddEditDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = strings.addTodo
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
            } else if (state.todos.isEmpty()) {
                // 할 일이 없는 경우
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = strings.todoEmpty,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                // 할 일 목록 표시
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
                    contentPadding = PaddingValues(16.dp)
                )
            }
            
            // 에러 메시지 표시
            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
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
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding
    ) {
        items(todos) { todo ->
            TodoItem(
                todo = todo,
                onToggleCompletion = { onToggleCompletion(todo) },
                onEdit = { onEdit(todo) },
                onDelete = { onDelete(todo) }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
