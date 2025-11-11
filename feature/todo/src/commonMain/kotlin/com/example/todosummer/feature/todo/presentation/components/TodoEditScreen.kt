package com.example.todosummer.feature.todo.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.logger.Logger

/**
 * Todo 항목을 추가하거나 편집하는 화면
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TodoEditScreen(
    todo: Todo?,
    categories: List<String>,
    onSave: (Todo) -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditing = todo != null
    
    var title by remember(todo) { mutableStateOf(todo?.title ?: "") }
    var priority by remember(todo) { mutableStateOf(todo?.priority ?: Priority.MEDIUM) }
    var selectedCategory by remember(todo) { mutableStateOf(todo?.category ?: "업무") }
    
    // 추가 옵션 상태
    var dueDate by remember(todo) { mutableStateOf<LocalDateTime?>(todo?.dueDate) }
    LaunchedEffect(todo?.id) {
        println("[TodoEdit] Loaded todo id=${todo?.id} dueDate=${dueDate}")
    }
    var hasReminder by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf("마감 10분 전") }
    var repeatOption by remember { mutableStateOf("안 함") }
    
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteCategoryDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<String?>(null) }
    var showAdvancedOptions by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "할 일 수정하기" else "새로운 할 일",
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = "닫기")
                }
            }
            
            // 할 일 입력
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("새로운 할 일 추가...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            // 카테고리 선택
            SectionTitle(title = "카테고리")
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        trailingIcon = if (selectedCategory == category) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "삭제",
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable {
                                            categoryToDelete = category
                                            showDeleteCategoryDialog = true
                                        },
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        } else null,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
                
                TextButton(onClick = { showAddCategoryDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "카테고리 추가",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("추가")
                }
            }
            
            // 추가 옵션 버튼
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdvancedOptions = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "추가 옵션",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "추가 옵션",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 저장 버튼
            Button(
                onClick = {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val newTodo = Todo(
                        id = todo?.id ?: "",
                        title = title,
                        isCompleted = todo?.isCompleted ?: false,
                        createdAt = todo?.createdAt ?: now,
                        updatedAt = if (isEditing) now else null,
                        dueDate = dueDate,
                        priority = priority,
                        category = selectedCategory
                    )
                    println("[TodoEdit] Saving todo id=${newTodo.id.ifEmpty { "<new>" }} dueDate=${newTodo.dueDate}")
                    onSave(newTodo)
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isEditing) "저장" else "추가하기",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // 카테고리 추가 다이얼로그
    if (showAddCategoryDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("새 카테고리 추가") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("카테고리 이름") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onAddCategory(newCategoryName)
                            selectedCategory = newCategoryName
                            newCategoryName = ""
                            showAddCategoryDialog = false
                        }
                    }
                ) {
                    Text("추가")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    newCategoryName = ""
                    showAddCategoryDialog = false 
                }) {
                    Text("취소")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
    
    // 카테고리 삭제 확인 다이얼로그
    if (showDeleteCategoryDialog && categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteCategoryDialog = false
                categoryToDelete = null
            },
            title = { 
                Text(
                    text = "카테고리를 삭제하시겠습니까?",
                    style = MaterialTheme.typography.titleLarge
                ) 
            },
            text = { 
                Text(
                    text = "'$categoryToDelete' 카테고리가 삭제됩니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        categoryToDelete?.let { onDeleteCategory(it) }
                        showDeleteCategoryDialog = false
                        categoryToDelete = null
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
                    showDeleteCategoryDialog = false
                    categoryToDelete = null
                }) {
                    Text("취소")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
    
    // 상세 설정 다이얼로그
    if (showAdvancedOptions) {
        AdvancedOptionsDialog(
            priority = priority,
            dueDate = dueDate,
            hasReminder = hasReminder,
            reminderTime = reminderTime,
            repeatOption = repeatOption,
            onPriorityChange = { priority = it },
            onDueDateChange = { dueDate = it },
            onReminderChange = { hasReminder = it },
            onReminderTimeChange = { reminderTime = it },
            onRepeatChange = { repeatOption = it },
            onDismiss = { showAdvancedOptions = false },
            onConfirm = { showAdvancedOptions = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedOptionsDialog(
    priority: Priority,
    dueDate: LocalDateTime?,
    hasReminder: Boolean,
    reminderTime: String,
    repeatOption: String,
    onPriorityChange: (Priority) -> Unit,
    onDueDateChange: (LocalDateTime?) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    onReminderTimeChange: (String) -> Unit,
    onRepeatChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "상세 설정",
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "닫기")
                }
            }
            
            // 마감일
            OptionItem(
                icon = Icons.Default.CalendarToday,
                title = "마감일",
                subtitle = dueDate?.let { formatDate(it) } ?: "설정 안 함",
                onClick = { showDatePicker = true }
            )
            
            // 알림
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "알림",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "알림",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Switch(
                    checked = hasReminder,
                    onCheckedChange = onReminderChange
                )
            }
            
            if (hasReminder) {
                OptionItem(
                    icon = null,
                    title = "알림 시간",
                    subtitle = reminderTime,
                    onClick = { /* 알림 시간 선택 */ },
                    modifier = Modifier.padding(start = 40.dp)
                )
            }
            
            // 중요도
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PriorityHigh,
                        contentDescription = "중요도",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "중요도",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Priority.entries.forEach { prio ->
                        FilterChip(
                            selected = priority == prio,
                            onClick = { onPriorityChange(prio) },
                            label = { Text(prio.toDisplayString()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (prio) {
                                    Priority.HIGH -> MaterialTheme.colorScheme.errorContainer
                                    Priority.MEDIUM -> MaterialTheme.colorScheme.primaryContainer
                                    Priority.LOW -> MaterialTheme.colorScheme.tertiaryContainer
                                }
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }
            
            // 설정 완료 버튼
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "설정 완료",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // 날짜 선택 다이얼로그
    if (showDatePicker) {
        // 현재 마감일이 있으면 그 날짜로, 없으면 오늘 날짜로 초기화
        val initialMillis = dueDate?.let { date ->
            val instant = date.toInstant(TimeZone.currentSystemDefault())
            instant.toEpochMilliseconds()
        } ?: Clock.System.now().toEpochMilliseconds()
        
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.fromEpochMilliseconds(millis)
                        val selectedDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                        // 시간은 현재 시간으로 설정 (날짜만 변경)
                        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        val finalDueDate = kotlinx.datetime.LocalDateTime(selectedDate.date, now.time)
                        println("[TodoEdit] Due date selected=$finalDueDate")
                        onDueDateChange(finalDueDate)
                    }
                    showDatePicker = false
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("취소")
                }
            },
            shape = RoundedCornerShape(20.dp)
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun OptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

private fun Priority.toDisplayString(): String {
    return when (this) {
        Priority.LOW -> "낮음"
        Priority.MEDIUM -> "보통"
        Priority.HIGH -> "높음"
    }
}

private fun formatDate(date: LocalDateTime): String {
    val hour = if (date.hour == 0) 12 else if (date.hour > 12) date.hour - 12 else date.hour
    val amPm = if (date.hour < 12) "오전" else "오후"
    val minute = date.minute.toString().padStart(2, '0')
    return "${date.year}년 ${date.monthNumber}월 ${date.dayOfMonth}일, $amPm $hour:$minute"
}
