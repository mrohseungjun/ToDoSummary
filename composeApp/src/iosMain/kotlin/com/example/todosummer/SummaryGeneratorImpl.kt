package com.example.todosummer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.todosummer.feature.ai.domain.SummaryGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS 플랫폼에서 SummaryGenerator 구현체를 생성합니다.
 * 현재는 기본 구현을 제공하며, 향후 iOS 전용 AI 기능으로 확장할 수 있습니다.
 */
@Composable
actual fun createSummaryGenerator(): SummaryGenerator {
    return remember { 
        object : SummaryGenerator {
            override suspend fun generateSummary(text: String, maxLength: Int): Result<String> {
                // iOS에서는 기본적인 요약을 제공
                return try {
                    val summary = if (text.isBlank()) {
                        "요약할 내용이 없습니다."
                    } else {
                        val words = text.split(" ")
                        val truncatedText = if (words.size > maxLength / 5) {
                            words.take(maxLength / 5).joinToString(" ") + "..."
                        } else {
                            text
                        }
                        "요약: $truncatedText"
                    }
                    Result.success(summary)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
            
            override fun generateSummaryStream(text: String, maxLength: Int): Flow<String> {
                val summary = if (text.isBlank()) {
                    "요약할 내용이 없습니다."
                } else {
                    val words = text.split(" ")
                    val truncatedText = if (words.size > maxLength / 5) {
                        words.take(maxLength / 5).joinToString(" ") + "..."
                    } else {
                        text
                    }
                    "요약: $truncatedText"
                }
                
                val flow = MutableStateFlow(summary)
                return flow.asStateFlow()
            }
            
            override fun isModelLoaded(): Boolean {
                return true // iOS에서는 항상 준비된 상태로 간주
            }
            
            override suspend fun loadModel(): Result<Boolean> {
                return Result.success(true) // iOS에서는 별도 모델 로딩이 필요 없음
            }
        }
    }
}
