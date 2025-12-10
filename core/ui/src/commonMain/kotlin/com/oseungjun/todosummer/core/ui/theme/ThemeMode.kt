package com.oseungjun.todosummer.core.ui.theme

/**
 * 앱에서 지원하는 테마 모드
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    companion object {
        /**
         * 문자열로부터 테마 모드를 반환합니다.
         */
        fun fromString(value: String): ThemeMode {
            return when (value.lowercase()) {
                "dark" -> DARK
                "light" -> LIGHT
                else -> SYSTEM
            }
        }
    }
}
