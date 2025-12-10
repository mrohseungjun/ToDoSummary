package com.oseungjun.todosummer.core.data.preferences

import com.oseungjun.todosummer.core.common.localization.LanguageMode

/**
 * 플랫폼별 언어 설정 영속화
 * Android: SharedPreferences에 저장 (attachBaseContext에서 사용)
 * iOS: 별도 처리 불필요
 */
expect fun persistLanguageMode(mode: LanguageMode)
