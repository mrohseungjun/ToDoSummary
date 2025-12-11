package com.oseungjun.todosummer

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.oseungjun.todosummer.di.initKoinIfNeeded
import com.oseungjun.todosummer.main.MainAppState
import com.oseungjun.todosummer.main.MainScreen
import com.oseungjun.todosummer.splash.SplashScreen
import com.oseungjun.todosummer.splash.rememberSplashLogoPainter
import com.oseungjun.todosummer.core.data.preferences.UserPreferencesRepository
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    // Koin 초기화 (플랫폼 별 actual에서 한 번만)
    initKoinIfNeeded()

    var showSplash by remember { mutableStateOf(true) }
    val logoPainter = rememberSplashLogoPainter()
    
    // 메인 화면용 상태 미리 초기화 (전환 시 깜빡임 방지)
    val preferencesRepository: UserPreferencesRepository = koinInject()
    val appState = remember { MainAppState(preferencesRepository) }

    MaterialTheme {
        Crossfade(
            targetState = showSplash,
            animationSpec = tween(400),
            label = "splash_transition"
        ) { isSplash ->
            if (isSplash) {
                SplashScreen(
                    onSplashFinished = { showSplash = false },
                    logoPainter = logoPainter
                )
            } else {
                MainScreen(
                    appState = appState
                )
            }
        }
    }
}
