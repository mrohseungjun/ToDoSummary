package com.example.todosummer.feature.todo.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.ui.AppIcons
import com.example.todosummer.core.ui.theme.priorityHigh
import com.example.todosummer.core.ui.theme.priorityLow
import com.example.todosummer.core.ui.theme.priorityMedium
import kotlinx.datetime.LocalDateTime

/**
 * Todo 항목을 표시하는 컴포저블
 */
@Composable
fun TodoItem(
    todo: Todo,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 완료 체크박스
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggleCompletion() }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 우선순위 표시
            PriorityIndicator(priority = todo.priority)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Todo 내용
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = todo.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // 마감일이 있는 경우 표시
                todo.dueDate?.let { dueDate ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${strings.todoDueDate}: ${formatDate(dueDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            
            // 액션 버튼
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = AppIcons.Edit,
                    contentDescription = strings.editTodo,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = AppIcons.Delete,
                    contentDescription = strings.deleteTodo,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * 우선순위를 시각적으로 표시하는 컴포저블
 */
@Composable
fun PriorityIndicator(priority: Priority) {
    val color = when (priority) {
        Priority.LOW -> priorityLow
        Priority.MEDIUM -> priorityMedium
        Priority.HIGH -> priorityHigh
    }
    
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * 날짜 포맷팅 함수
 */
private fun formatDate(date: LocalDateTime): String {
    return "${date.year}년 ${date.monthNumber}월 ${date.dayOfMonth}일"
}
