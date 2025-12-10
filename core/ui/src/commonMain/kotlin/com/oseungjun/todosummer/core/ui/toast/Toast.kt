package com.oseungjun.todosummer.core.ui.toast

import androidx.compose.runtime.Composable

/**
 * 플랫폼별 Toast 메시지 표시
 */
expect fun showToast(message: String)

/**
 * Composable 내에서 Toast를 표시하기 위한 래퍼
 */
@Composable
expect fun rememberToastState(): ToastState

expect class ToastState {
    fun show(message: String)
}
