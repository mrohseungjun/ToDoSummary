package com.oseungjun.todosummer.core.ui.util

import androidx.compose.runtime.Composable
import com.oseungjun.todosummer.core.common.localization.LanguageMode

/**
 * 언어 설정을 동기적으로 저장하는 함수를 반환
 * Activity 재시작 전에 반드시 저장 완료되어야 함
 */
@Composable
expect fun rememberSaveLanguage(): (LanguageMode) -> Unit
