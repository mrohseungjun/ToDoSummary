package com.example.todosummer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.todosummer.di.initKoinIfNeeded
import com.example.todosummer.main.MainAppState
import com.example.todosummer.main.MainScreen
import com.example.todosummer.core.data.preferences.UserPreferencesRepository
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    // Koin 초기화 (플랫폼 별 actual에서 한 번만)
    initKoinIfNeeded()

    // 앱 전역 상태
    val preferencesRepository: UserPreferencesRepository = koinInject()
    val appState = remember { MainAppState(preferencesRepository) }

    MaterialTheme {
        MainScreen(
            appState = appState
        )
    }
}
