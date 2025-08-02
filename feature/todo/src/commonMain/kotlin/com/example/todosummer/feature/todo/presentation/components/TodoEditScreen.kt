package com.example.todosummer.feature.todo.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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

/**
 * Todo 항목을 추가하거나 편집하는 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditScreen(
    todo: Todo?,
    onSave: (Todo) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    val isEditing = todo != null
    
    var title by remember { mutableStateOf(todo?.title ?: "") }
    var description by remember { mutableStateOf(todo?.description ?: "") }
    var priority by remember { mutableStateOf(todo?.priority ?: Priority.MEDIUM) }
    var dueDate by remember { mutableStateOf(todo?.dueDate) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) strings.editTodo else strings.addTodo) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 제목 입력
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(strings.todoTitle) },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 설명 입력
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(strings.todoDescription) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            // 우선순위 선택
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = when (priority) {
                        Priority.LOW -> strings.priorityLow
                        Priority.MEDIUM -> strings.priorityMedium
                        Priority.HIGH -> strings.priorityHigh
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(strings.todoPriority) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(strings.priorityLow) },
                        onClick = {
                            priority = Priority.LOW
                            isDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(strings.priorityMedium) },
                        onClick = {
                            priority = Priority.MEDIUM
                            isDropdownExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(strings.priorityHigh) },
                        onClick = {
                            priority = Priority.HIGH
                            isDropdownExpanded = false
                        }
                    )
                }
            }
            
            // 마감일 선택
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.todoDueDate,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                TextButton(onClick = { showDatePicker = true }) {
                    Text(
                        text = dueDate?.let { formatDate(it) } ?: "선택하세요"
                    )
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
                                dueDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                            }
                            showDatePicker = false
                        }) {
                            Text(strings.confirm)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(strings.cancel)
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text(strings.cancel)
                }
                
                Button(
                    onClick = {
                        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        val newTodo = Todo(
                            id = todo?.id ?: "",
                            title = title,
                            description = description,
                            isCompleted = todo?.isCompleted ?: false,
                            createdAt = todo?.createdAt ?: now,
                            updatedAt = if (isEditing) now else null,
                            dueDate = dueDate,
                            priority = priority,
                            tags = todo?.tags ?: emptyList()
                        )
                        onSave(newTodo)
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text(strings.save)
                }
            }
        }
    }
}

/**
 * 날짜 포맷팅 함수
 */
private fun formatDate(date: LocalDateTime): String {
    return "${date.year}년 ${date.monthNumber}월 ${date.dayOfMonth}일"
}
