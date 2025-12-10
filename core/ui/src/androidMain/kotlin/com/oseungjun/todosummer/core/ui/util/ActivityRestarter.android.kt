package com.oseungjun.todosummer.core.ui.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Android Activity 재시작 함수를 반환
 */
@Composable
actual fun rememberRestartActivity(): () -> Unit {
    val context = LocalContext.current
    return remember(context) {
        {
            val activity = context as? Activity ?: return@remember
            val intent = activity.intent
            activity.finish()
            activity.startActivity(intent)
            activity.overridePendingTransition(0, 0)
        }
    }
}
