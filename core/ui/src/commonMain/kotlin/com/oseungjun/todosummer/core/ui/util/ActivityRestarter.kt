package com.oseungjun.todosummer.core.ui.util

import androidx.compose.runtime.Composable

/**
 * Activity 재시작 함수를 반환하는 Composable
 * 언어 변경 시 사용
 */
@Composable
expect fun rememberRestartActivity(): () -> Unit
