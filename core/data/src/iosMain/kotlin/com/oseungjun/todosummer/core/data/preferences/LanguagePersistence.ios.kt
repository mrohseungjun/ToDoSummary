package com.oseungjun.todosummer.core.data.preferences

import com.oseungjun.todosummer.core.common.localization.LanguageMode

/**
 * iOS: 별도 영속화 불필요 (DataStore만 사용)
 */
actual fun persistLanguageMode(mode: LanguageMode) {
    // iOS에서는 별도 처리 불필요
}
