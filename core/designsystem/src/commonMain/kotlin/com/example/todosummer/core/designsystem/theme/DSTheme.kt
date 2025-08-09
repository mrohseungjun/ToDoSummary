package com.example.todosummer.core.designsystem.theme

import androidx.compose.runtime.Composable

/**
 * 프로젝트 공통 디자인 시스템 테마 래퍼.
 * 현재는 MaterialTheme 기본 값을 사용하지만, 추후 색상/타이포/쉐이프를 분리하여 주입하도록 확장 예정.
 */
@Composable
fun DSTheme(content: @Composable () -> Unit) {
    // 현재 단계에서는 추가 CompositionLocal 없이 단순 래핑만 수행합니다.
    // MaterialTheme 제공은 상위(AppTheme 등)에서 담당합니다.
    content()
}
