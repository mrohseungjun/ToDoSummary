package com.example.todosummer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.todosummer.feature.ai.presentation.SummaryViewModel
import com.example.todosummer.di.initKoinIfNeeded
import com.example.todosummer.main.MainScreen
import com.example.todosummer.main.MainAppState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    // Koin 초기화 (플랫폼 별 actual에서 한 번만)
    initKoinIfNeeded()

    // 앱 전역 상태
    val appState = remember { MainAppState() }
    val coroutineScope = rememberCoroutineScope()

    val summaryGenerator = createSummaryGenerator()
    val summaryViewModel = remember(summaryGenerator) { SummaryViewModel(summaryGenerator, coroutineScope) }

    MainScreen(
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