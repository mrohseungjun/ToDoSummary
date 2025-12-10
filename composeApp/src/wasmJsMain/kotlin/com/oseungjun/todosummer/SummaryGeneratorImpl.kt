package com.oseungjun.todosummer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.oseungjun.todosummer.feature.ai.domain.SummaryGenerator

/**
 * WASM/JS 플랫폼에서 SummaryGenerator 구현체를 생성합니다.
 * 현재는 기본 구현을 제공하며, 향후 웹 기반 AI API와 연동할 수 있습니다.
 */
@Composable
actual fun createSummaryGenerator(): SummaryGenerator {
    return remember { 
        object : SummaryGenerator {
            override suspend fun generateSummary(todos: List<String>): String {
                // WASM/JS에서는 기본적인 요약을 제공
                return if (todos.isEmpty()) {
                    "할 일이 없습니다."
                } else {
                    "총 ${todos.size}개의 할 일이 있습니다:\n${todos.take(3).joinToString("\n") { "• $it" }}" +
                    if (todos.size > 3) "\n... 그리고 ${todos.size - 3}개 더" else ""
                }
            }
        }
    }
}
