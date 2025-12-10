package com.oseungjun.todosummer.core.common.localization

/**
 * 앱에서 지원하는 언어 모드
 */
enum class LanguageMode {
    ENGLISH,
    KOREAN;
    
    companion object {
        /**
         * 문자열로부터 언어 모드를 반환합니다.
         */
        fun fromString(value: String): LanguageMode {
            return when (value.lowercase()) {
                "korean", "ko", "kr" -> KOREAN
                else -> ENGLISH
            }
        }
    }
}
