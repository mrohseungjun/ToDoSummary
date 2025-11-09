package com.example.todosummer.feature.todo.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberModalBottomSheetState
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
    
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 헤더 타이틀
            Text(
                text = if (isEditing) "할 일 수정하기" else "새로운 할 일",
                style = MaterialTheme.typography.titleLarge
            )
            
            // 할 일 입력
            Text(
                text = "할 일",
                style = MaterialTheme.typography.bodyLarge
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("새로운 할 일 추가...") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // 카테고리 선택
            Text(
                text = "카테고리",
                style = MaterialTheme.typography.bodyLarge
            )
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
                
                // 추가 버튼
                FilterChip(
                    selected = false,
                    onClick = { showAddCategoryDialog = true },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "카테고리 추가",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text("추가")
                        }
                    }
                )
            }
            
            // 추가 옵션 (우선순위)
            Text(
                text = "추가 옵션",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 우선순위 아이콘 버튼들
                IconButton(
                    onClick = { priority = Priority.LOW },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📅", style = MaterialTheme.typography.headlineMedium)
                        Text("낮음", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                IconButton(
                    onClick = { priority = Priority.MEDIUM },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔔", style = MaterialTheme.typography.headlineMedium)
                        Text("보통", style = MaterialTheme.typography.bodySmall)
                    }
                }
                
                IconButton(
                    onClick = { priority = Priority.HIGH },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🚩", style = MaterialTheme.typography.headlineMedium)
                        Text("높음", style = MaterialTheme.typography.bodySmall)
                    }
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
                            isCompleted = todo?.isCompleted ?: false,
                            createdAt = todo?.createdAt ?: now,
                            updatedAt = if (isEditing) now else null,
                            priority = priority,
                            category = selectedCategory
                        )
                        onSave(newTodo)
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text(if (isEditing) "저장" else "추가하기")
                }
            }
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
                    singleLine = true
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
            }
        )
    }
}
