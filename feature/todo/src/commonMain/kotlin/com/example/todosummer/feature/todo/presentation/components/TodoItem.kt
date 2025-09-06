package com.example.todosummer.feature.todo.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.domain.model.Priority
import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.ui.AppIcons
import com.example.todosummer.core.ui.theme.Dimens
import com.example.todosummer.core.ui.theme.priorityHigh
import com.example.todosummer.core.ui.theme.priorityLow
import com.example.todosummer.core.ui.theme.priorityMedium
import kotlinx.datetime.LocalDateTime
import kotlin.math.abs
import kotlin.math.roundToInt

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
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val revealWidth = Dimens.size64
    val revealPx = remember(revealWidth, density) { with(density) { revealWidth.toPx() } }
    val offsetX = remember { Animatable(0f) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spacing4)
            ,
        shape = RoundedCornerShape(Dimens.radius8),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevation2)
    ) {
        Box {
            // 배경: 삭제 액션 영역 (오른쪽)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .width(revealWidth)
                        .height(72.dp)
                        .background(MaterialTheme.colorScheme.error)
                        .clickable {
                            // 삭제 실행 후 닫기
                            onDelete()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Delete,
                        contentDescription = strings.deleteTodo,
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }

            // 전경: 실제 아이템 콘텐츠 (드래그로 좌측 이동)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount ->
                                val target = (offsetX.value + dragAmount)
                                    .coerceIn(-revealPx, 0f)
                                scope.launch {
                                    offsetX.snapTo(target)
                                }
                            },
                            onDragEnd = {
                                val shouldOpen = abs(offsetX.value) > revealPx * 0.5f
                                scope.launch {
                                    offsetX.animateTo(
                                        targetValue = if (shouldOpen) -revealPx else 0f,
                                        animationSpec = tween(durationMillis = 160)
                                    )
                                }
                            }
                        )
                    }
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(Dimens.spacing12),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 완료 체크박스
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { onToggleCompletion() }
                )

                Spacer(modifier = Modifier.width(Dimens.spacing8))

                // 우선순위 표시
                PriorityIndicator(priority = todo.priority)

                Spacer(modifier = Modifier.width(Dimens.spacing12))

                // Todo 내용
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onEdit() }
                ) {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(Dimens.spacing4))

                    Text(
                        text = todo.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // 마감일이 있는 경우 표시
                    todo.dueDate?.let { dueDate ->
                        Spacer(modifier = Modifier.height(Dimens.spacing4))
                        Text(
                            text = "${strings.todoDueDate}: ${formatDate(dueDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
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
            .size(Dimens.size16)
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
