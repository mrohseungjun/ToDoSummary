package com.example.todosummer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.todosummer.core.data.repository.TodoRepositoryImpl
import com.example.todosummer.core.data.source.local.TodoLocalDataSource
import com.example.todosummer.core.domain.usecase.TodoUseCases
import com.example.todosummer.feature.ai.presentation.SummaryViewModel
import com.example.todosummer.feature.todo.presentation.TodoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // 앱 상태 관리
    val appState = remember { AppState() }
    
    // 코루틴 스코프
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    // Todo 관련 의존성
    val todoDataSource = remember { TodoLocalDataSource() }
    val todoRepository = remember { TodoRepositoryImpl(todoDataSource) }
    val todoUseCases = remember { TodoUseCases(todoRepository) }
    val todoViewModel = remember { TodoViewModel(todoUseCases, coroutineScope) }
    
    // AI 요약 관련 의존성
    val summaryGenerator = createSummaryGenerator()
    val summaryViewModel = remember { SummaryViewModel(summaryGenerator, coroutineScope) }
    
    // 메인 화면 표시
    MainScreen(
        todoViewModel = todoViewModel,
        summaryViewModel = summaryViewModel,
        appState = appState
    )
}

/**
 * 플랫폼에 맞는 SummaryGenerator를 생성합니다.
 * 실제 구현은 각 플랫폼별 소스셋에서 제공됩니다.
 */
@Composable
expect fun createSummaryGenerator(): com.example.todosummer.feature.ai.domain.SummaryGenerator