package com.example.todosummer.feature.ai.data

import android.content.Context
import com.example.todosummer.feature.ai.domain.SummaryGenerator
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Gemma 3 모델을 사용한 요약 생성기 구현
 */
class GemmaSummaryGenerator(
    private val context: Context,
    private val apiKey: String
) : SummaryGenerator {
    
    private var model: GenerativeModel? = null
    private var isLoaded = false
    
    override suspend fun generateSummary(text: String, maxLength: Int): Result<String> {
        return try {
            if (!isModelLoaded()) {
                val loadResult = loadModel()
                if (loadResult.isFailure) {
                    return Result.failure(loadResult.exceptionOrNull() ?: Exception("Failed to load model"))
                }
            }
            
            val prompt = buildSummaryPrompt(text, maxLength)
            val response = model?.generateContent(prompt)?.text ?: ""
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun generateSummaryStream(text: String, maxLength: Int): Flow<String> = flow {
        if (!isModelLoaded()) {
            loadModel().getOrThrow()
        }
        
        val prompt = buildSummaryPrompt(text, maxLength)
        model?.generateContentStream(prompt)?.map { it.text ?: "" }?.collect {
            emit(it)
        }
    }
    
    override fun isModelLoaded(): Boolean {
        return isLoaded && model != null
    }
    
    override suspend fun loadModel(): Result<Boolean> {
        return try {
            model = GenerativeModel(
                modelName = "gemma-3-2b-it",
                apiKey = apiKey,
                generationConfig = GenerationConfig(
                    temperature = 0.4f,
                    topK = 32,
                    topP = 0.95f,
                    maxOutputTokens = 1024
                )
            )
            isLoaded = true
            Result.success(true)
        } catch (e: Exception) {
            isLoaded = false
            Result.failure(e)
        }
    }
    
    /**
     * 요약을 위한 프롬프트를 생성합니다.
     */
    private fun buildSummaryPrompt(text: String, maxLength: Int): String {
        return """
            당신은 텍스트를 간결하게 요약하는 AI 도우미입니다.
            다음 텍스트를 최대 $maxLength자 이내로 요약해주세요.
            중요한 내용만 포함하고, 불필요한 세부 사항은 생략하세요.
            
            텍스트:
            $text
            
            요약:
        """.trimIndent()
    }
}
