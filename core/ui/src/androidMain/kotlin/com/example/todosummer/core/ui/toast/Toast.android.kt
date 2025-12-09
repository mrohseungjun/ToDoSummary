package com.example.todosummer.core.ui.toast

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Android Toast 표시
 */
actual fun showToast(message: String) {
    // Context가 없으면 동작하지 않음 - Composable 내에서는 rememberToastState 사용
}

@Composable
actual fun rememberToastState(): ToastState {
    val context = LocalContext.current
    return remember { ToastState(context) }
}

actual class ToastState(private val context: android.content.Context) {
    actual fun show(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
