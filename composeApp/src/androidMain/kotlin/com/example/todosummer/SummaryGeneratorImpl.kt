package com.example.todosummer

import android.content.Context
import com.example.todosummer.feature.ai.data.GemmaSummaryGenerator
import com.example.todosummer.feature.ai.domain.SummaryGenerator
import org.jetbrains.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * 안드로이드 플랫폼에서 SummaryGenerator 구현체를 생성합니다.
 */
@Composable
actual fun createSummaryGenerator(): SummaryGenerator {
    val context = LocalContext.current
    // 실제 API 키는 안전하게 관리해야 합니다. 여기서는 예시로 빈 문자열을 사용합니다.
    // 실제 앱에서는 BuildConfig나 안전한 저장소에서 가져와야 합니다.
    val apiKey = ""
    return remember { GemmaSummaryGenerator(context, apiKey) }
}
