package com.example.todosummer.core.ui.util

import androidx.compose.runtime.Composable

/**
 * iOS에서는 Activity 개념이 없으므로 no-op
 * iOS는 Locale 변경이 즉시 반영됨
 */
@Composable
actual fun rememberRestartActivity(): () -> Unit {
    return { /* iOS에서는 별도 처리 불필요 */ }
}
