package com.oseungjun.todosummer.di

import com.oseungjun.todosummer.feature.statistics.domain.StatisticsGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.dsl.module

actual val platformModule = module {
    single<StatisticsGenerator> {
        object : StatisticsGenerator {
            override suspend fun generateStatistics(text: String, maxLength: Int): Result<String> {
                return try {
                    val statistics = if (text.isBlank()) {
                        "통계낼 내용이 없습니다."
                    } else {
                        val words = text.split(" ")
                        val truncatedText = if (words.size > maxLength / 5) {
                            words.take(maxLength / 5).joinToString(" ") + "..."
                        } else {
                            text
                        }
                        "통계: $truncatedText"
                    }
                    Result.success(statistics)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

            override fun generateStatisticsStream(text: String, maxLength: Int): Flow<String> {
                val statistics = if (text.isBlank()) {
                    "통계낼 내용이 없습니다."
                } else {
                    val words = text.split(" ")
                    val truncatedText = if (words.size > maxLength / 5) {
                        words.take(maxLength / 5).joinToString(" ") + "..."
                    } else {
                        text
                    }
                    "통계: $truncatedText"
                }

                val flow = MutableStateFlow(statistics)
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
