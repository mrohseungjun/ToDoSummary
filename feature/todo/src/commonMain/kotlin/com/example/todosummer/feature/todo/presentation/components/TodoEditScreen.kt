package com.example.todosummer.feature.todo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Todo 항목을 추가하거나 편집하는 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditScreen(
    todo: Todo?,
    categories: List<String>,
    onSave: (Todo) -> Unit,
    onAddCategory: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val isEditing = todo != null
    
    var title by remember { mutableStateOf(todo?.title ?: "") }
    var priority by remember { mutableStateOf(todo?.priority ?: Priority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf(todo?.category ?: "업무") }
    
    // 추가 옵션 상태
    var dueDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var hasReminder by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf("마감 10분 전") }
    var repeatOption by remember { mutableStateOf("안 함") }
    
    var showAddCategoryDialog by remember { mutableStateOf(false) }
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
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
                        priority = priority,
                        category = selectedCategory
                    )
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
                subtitle = dueDate?.let { formatDate(it) } ?: "2025년 10월 27일, 오후 3:00",
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
            
            // 반복
            OptionItem(
                icon = Icons.Default.Repeat,
                title = "반복",
                subtitle = repeatOption,
                onClick = { /* 반복 옵션 선택 */ }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
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
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.fromEpochMilliseconds(millis)
                        onDueDateChange(instant.toLocalDateTime(TimeZone.currentSystemDefault()))
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
    return "${date.year}년 ${date.monthNumber}월 ${date.dayOfMonth}일"
}
