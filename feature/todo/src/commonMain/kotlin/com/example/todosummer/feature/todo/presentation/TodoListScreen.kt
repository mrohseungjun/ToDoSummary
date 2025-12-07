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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.AlertDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    var showBulkDeleteDialog by remember { mutableStateOf(false) }
    var currentTodo by remember { mutableStateOf<Todo?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    
    // 선택 모드 상태
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedIds = state.selectedIds
    
    // 선택된 날짜 (null이면 오늘)
    val selectedDate = state.selectedDate ?: today.date
    
    // 날짜별 필터링 + 정렬/필터 + 검색 적용된 Todo 목록
    val filteredTodos = remember(
        state.todos,
        selectedDate,
        today.date,
        state.sortType,
        state.filterType,
        state.filterCategory,
        searchQuery
    ) {
        state.todos
            // 1. 날짜 필터링
            .filter { todo ->
                val todoDate = todo.createdAt.date
                val todoDueDate = todo.dueDate?.date
                val isToday = selectedDate == today.date
                val isPast = selectedDate < today.date
                
                // 완료된 항목: 완료한 날(updatedAt) 또는 생성일이 선택된 날짜와 같을 때만 표시
                // 하루 지나면 안 보임
                if (todo.isCompleted) {
                    val completedDate = todo.updatedAt?.date ?: todoDate
                    return@filter completedDate == selectedDate || todoDate == selectedDate
                }

                when {
                    isToday -> {
                        // 오늘: 오늘 생성 + 이전 미완료 + 마감일이 오늘
                        (todoDate == selectedDate) ||
                        (todoDate < selectedDate) ||
                        (todoDueDate == selectedDate)
                    }
                    isPast -> todoDate == selectedDate
                    else -> todoDate == selectedDate || todoDueDate == selectedDate
                }
            }
            // 2. 완료/미완료 필터
            .filter { todo ->
                when (state.filterType) {
                    FilterType.ALL -> true
                    FilterType.COMPLETED -> todo.isCompleted
                    FilterType.INCOMPLETE -> !todo.isCompleted
                }
            }
            // 3. 카테고리 필터
            .filter { todo ->
                state.filterCategory?.let { todo.category == it } ?: true
            }
            // 4. 검색 필터
            .filter { todo ->
                if (searchQuery.isBlank()) {
                    true
                } else {
                    val q = searchQuery.lowercase()
                    todo.title.lowercase().contains(q) ||
                        todo.category.lowercase().contains(q)
                }
            }
            // 5. 정렬
            .let { list ->
                when (state.sortType) {
                    SortType.CREATED_AT -> list.sortedByDescending { it.createdAt }
                    SortType.DUE_DATE -> list.sortedBy { it.dueDate ?: kotlinx.datetime.LocalDateTime(9999, 12, 31, 23, 59) }
                    SortType.PRIORITY -> list.sortedByDescending { it.priority.ordinal }
                    SortType.TITLE -> list.sortedBy { it.title.lowercase() }
                }
            }
    }
    
    // 이전 날짜 항목 ID 목록 (오늘 화면에서 "이전 항목" 표시용)
    val pastTodoIds = remember(filteredTodos, selectedDate, today.date) {
        if (selectedDate == today.date) {
            filteredTodos
                .filter { it.createdAt.date < today.date && !it.isCompleted }
                .map { it.id }
                .toSet()
        } else {
            emptySet()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 선택 모드 표시 바
            if (isSelectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing12),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = strings.selectionMode,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        if (selectedIds.isNotEmpty()) {
                            Text(
                                text = "(${selectedIds.size}${strings.itemsSelected})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                    
                    TextButton(
                        onClick = {
                            isSelectionMode = false
                            viewModel.onIntent(TodoIntent.ClearSelection)
                        }
                    ) {
                        Text(
                            text = strings.cancel,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // 상단 날짜 네비게이션 바 + 오늘 버튼 + 선택 모드 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = Dimens.spacing8),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 날짜 네비게이션
                DateNavigationBar(
                    dateText = "${selectedDate.monthNumber}월 ${selectedDate.dayOfMonth}일",
                    onPreviousDay = { viewModel.onIntent(TodoIntent.NavigateToPreviousDate) },
                    onNextDay = { viewModel.onIntent(TodoIntent.NavigateToNextDate) },
                    modifier = Modifier.weight(1f)
                )
                
                // 오늘 버튼 (오늘이 아닐 때만 표시)
                if (selectedDate != today.date) {
                    TextButton(
                        onClick = { viewModel.onIntent(TodoIntent.NavigateToToday) }
                    ) {
                        Text(
                            text = strings.today,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // 선택 모드 토글 버튼
                IconButton(
                    onClick = {
                        isSelectionMode = !isSelectionMode
                        if (!isSelectionMode) {
                            viewModel.onIntent(TodoIntent.ClearSelection)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Checklist,
                        contentDescription = strings.selectionMode,
                        tint = if (isSelectionMode) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // 정렬/필터 바
            SortFilterBar(
                sortType = state.sortType,
                filterType = state.filterType,
                filterCategory = state.filterCategory,
                categories = state.categories.map { it.name },
                onSortTypeChange = { viewModel.onIntent(TodoIntent.SetSortType(it)) },
                onFilterTypeChange = { viewModel.onIntent(TodoIntent.SetFilterType(it)) },
                onFilterCategoryChange = { viewModel.onIntent(TodoIntent.SetFilterCategory(it)) }
            )

            // 검색 바
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = strings.searchPlaceholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing8),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
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
                        onOpenStatistics = onOpenStatistics,
                        isSelectionMode = isSelectionMode,
                        selectedIds = selectedIds,
                        onSelectionToggle = { todo ->
                            viewModel.onIntent(TodoIntent.ToggleSelection(todo.id))
                        },
                        pastTodoIds = pastTodoIds
                    )
                }
            }
        }

        // FAB (선택 모드가 아닐 때만 표시)
        if (!isSelectionMode) {
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
        
        // 일괄 작업 바 (선택 모드일 때 하단에 표시)
        AnimatedVisibility(
            visible = isSelectionMode && selectedIds.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BulkActionBar(
                selectedCount = selectedIds.size,
                onCompleteAll = {
                    viewModel.onIntent(TodoIntent.CompleteSelected)
                    isSelectionMode = false
                },
                onDeleteAll = { showBulkDeleteDialog = true },
                onCancel = {
                    viewModel.onIntent(TodoIntent.ClearSelection)
                    isSelectionMode = false
                }
            )
        }
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
    
    // 일괄 삭제 확인 다이얼로그
    if (showBulkDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteDialog = false },
            title = { 
                Text(
                    text = "${selectedIds.size}개 항목을 삭제하시겠습니까?",
                    style = MaterialTheme.typography.titleLarge
                ) 
            },
            text = { 
                Text(
                    text = "선택한 모든 할 일이 삭제됩니다. 이 작업은 되돌릴 수 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onIntent(TodoIntent.DeleteSelected)
                        showBulkDeleteDialog = false
                        isSelectionMode = false
                    }
                ) {
                    Text(
                        text = "삭제",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteDialog = false }) {
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
    contentPadding: PaddingValues = PaddingValues(),
    isSelectionMode: Boolean = false,
    selectedIds: Set<String> = emptySet(),
    onSelectionToggle: (Todo) -> Unit = {},
    pastTodoIds: Set<String> = emptySet()
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            top = Dimens.spacing16,
            start = Dimens.spacing16,
            end = Dimens.spacing16,
            bottom = if (isSelectionMode && selectedIds.isNotEmpty()) 80.dp else Dimens.spacing16
        )
    ) {
        items(items = todos, key = { it.id }) { todo ->
            TodoItem(
                todo = todo,
                onToggleCompletion = { onToggleCompletion(todo) },
                onEdit = { onEdit(todo) },
                onDelete = { onDelete(todo) },
                modifier = Modifier.padding(bottom = Dimens.spacing12),
                isSelectionMode = isSelectionMode,
                isSelected = selectedIds.contains(todo.id),
                onSelectionToggle = { onSelectionToggle(todo) },
                isPastItem = pastTodoIds.contains(todo.id)
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

/**
 * 정렬/필터 드롭다운 바
 */
@Composable
private fun SortFilterBar(
    sortType: SortType,
    filterType: FilterType,
    filterCategory: String?,
    categories: List<String>,
    onSortTypeChange: (SortType) -> Unit,
    onFilterTypeChange: (FilterType) -> Unit,
    onFilterCategoryChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing8),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
    ) {
        // 정렬 드롭다운
        Box {
            FilterChip(
                selected = sortType != SortType.CREATED_AT,
                onClick = { showSortMenu = true },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (sortType) {
                                SortType.CREATED_AT -> strings.sortCreatedAt
                                SortType.DUE_DATE -> strings.sortDueDate
                                SortType.PRIORITY -> strings.sortPriority
                                SortType.TITLE -> strings.sortTitle
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(strings.sortCreatedAt) },
                    onClick = {
                        onSortTypeChange(SortType.CREATED_AT)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(strings.sortDueDate) },
                    onClick = {
                        onSortTypeChange(SortType.DUE_DATE)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(strings.sortPriority) },
                    onClick = {
                        onSortTypeChange(SortType.PRIORITY)
                        showSortMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(strings.sortTitle) },
                    onClick = {
                        onSortTypeChange(SortType.TITLE)
                        showSortMenu = false
                    }
                )
            }
        }
        
        // 완료/미완료 필터 드롭다운
        Box {
            FilterChip(
                selected = filterType != FilterType.ALL,
                onClick = { showFilterMenu = true },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (filterType) {
                                FilterType.ALL -> strings.filterAll
                                FilterType.COMPLETED -> strings.filterCompleted
                                FilterType.INCOMPLETE -> strings.filterIncomplete
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(strings.filterAll) },
                    onClick = {
                        onFilterTypeChange(FilterType.ALL)
                        showFilterMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(strings.filterCompleted) },
                    onClick = {
                        onFilterTypeChange(FilterType.COMPLETED)
                        showFilterMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(strings.filterIncomplete) },
                    onClick = {
                        onFilterTypeChange(FilterType.INCOMPLETE)
                        showFilterMenu = false
                    }
                )
            }
        }
        
        // 카테고리 필터 드롭다운
        if (categories.isNotEmpty()) {
            Box {
                FilterChip(
                    selected = filterCategory != null,
                    onClick = { showCategoryMenu = true },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = filterCategory ?: strings.filterCategory,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
                DropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(strings.filterAll) },
                        onClick = {
                            onFilterCategoryChange(null)
                            showCategoryMenu = false
                        }
                    )
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                onFilterCategoryChange(category)
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 일괄 작업 바 - 선택된 항목에 대한 완료/삭제 버튼
 */
@Composable
private fun BulkActionBar(
    selectedCount: Int,
    onCompleteAll: () -> Unit,
    onDeleteAll: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing12)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 선택된 개수 표시
        Text(
            text = "${selectedCount}개 선택됨",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
        ) {
            // 취소 버튼
            TextButton(onClick = onCancel) {
                Text(
                    text = "취소",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 완료 버튼
            TextButton(onClick = onCompleteAll) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "완료",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // 삭제 버튼
            TextButton(onClick = onDeleteAll) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "삭제",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
