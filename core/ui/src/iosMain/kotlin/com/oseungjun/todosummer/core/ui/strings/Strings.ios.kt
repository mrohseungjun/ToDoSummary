package com.oseungjun.todosummer.core.ui.strings

import platform.Foundation.NSUserDefaults

private const val KEY_LANGUAGE = "app_language_mode"

actual fun getCurrentLanguage(): String {
    // 앱 내 저장된 언어 설정 우선 사용
    val storedLanguage = NSUserDefaults.standardUserDefaults.stringForKey(KEY_LANGUAGE)
    return when (storedLanguage) {
        "KOREAN" -> "ko"
        "ENGLISH" -> "en"
        else -> "ko" // 기본값: 한국어
    }
}
