package com.example.todosummer.core.ui.util

import androidx.compose.runtime.Composable
import com.example.todosummer.core.common.localization.LanguageMode
import platform.Foundation.NSUserDefaults

private const val KEY_LANGUAGE = "app_language_mode"

/**
 * iOS: NSUserDefaults에 언어 설정 저장
 */
@Composable
actual fun rememberSaveLanguage(): (LanguageMode) -> Unit {
    return { mode: LanguageMode ->
        NSUserDefaults.standardUserDefaults.setObject(mode.name, KEY_LANGUAGE)
        NSUserDefaults.standardUserDefaults.synchronize()
    }
}

/**
 * iOS에서 저장된 언어 설정 읽기
 */
fun getStoredLanguageMode(): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey(KEY_LANGUAGE)
}
