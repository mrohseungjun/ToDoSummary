package com.example.todosummer.core.ui.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.todosummer.core.common.localization.LanguageMode

private const val PREFS_NAME = "language_prefs"
private const val KEY_LANGUAGE = "language_mode"

/**
 * Android: SharedPreferences에 동기적으로 언어 저장
 */
@Composable
actual fun rememberSaveLanguage(): (LanguageMode) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { mode: LanguageMode ->
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_LANGUAGE, mode.name)
                .commit() // apply() 대신 commit() 사용하여 동기적으로 저장
        }
    }
}
