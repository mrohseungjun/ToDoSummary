package com.example.todosummer.core.data.ai

import com.example.todosummer.core.domain.ai.AIReport
import com.example.todosummer.core.domain.ai.GeminiService
import com.example.todosummer.core.domain.ai.ProcrastinationPatterns
import com.example.todosummer.core.domain.model.Todo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Gemini 2.5 Flash API를 사용한 AI 서비스 구현
 */
class GeminiServiceImpl(
    private val httpClient: HttpClient,
    private val apiKey: String
) : GeminiService {
    
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
    
    override suspend fun generateReport(
        todos: List<Todo>,
        periodLabel: String
    ): Result<AIReport> {
        return try {
            val prompt = buildReportPrompt(todos, periodLabel)
            val response = callGeminiApi(prompt)
            val report = parseReportResponse(response)
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun analyzeProcrastination(
        todos: List<Todo>
    ): Result<ProcrastinationPatterns> {
        return try {
            val prompt = buildProcrastinationPrompt(todos)
            val response = callGeminiApi(prompt)
            val patterns = parseProcrastinationResponse(response)
            Result.success(patterns)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun buildReportPrompt(todos: List<Todo>, periodLabel: String): String {
        val completedTodos = todos.filter { it.isCompleted }
        val incompleteTodos = todos.filter { !it.isCompleted }
        val categoryStats = todos.groupBy { it.category }.mapValues { it.value.size }
        
        return """
            당신은 생산성 코치입니다. 사용자의 $periodLabel Todo 데이터를 분석해주세요.
            
            ## 데이터
            - 총 할 일: ${todos.size}개
            - 완료: ${completedTodos.size}개
            - 미완료: ${incompleteTodos.size}개
            - 카테고리별: ${categoryStats.entries.joinToString(", ") { "${it.key}: ${it.value}개" }}
            
            ## 완료된 할 일
            ${completedTodos.take(10).joinToString("\n") { "- ${it.title} (${it.category}, ${it.priority})" }}
            
            ## 미완료 할 일
            ${incompleteTodos.take(10).joinToString("\n") { "- ${it.title} (${it.category}, ${it.priority})" }}
            
            ## 응답 형식 (JSON)
            다음 JSON 형식으로만 응답해주세요. 다른 텍스트 없이 JSON만 출력하세요:
            {
                "summary": "한 줄 요약 (50자 이내)",
                "insights": ["인사이트1", "인사이트2", "인사이트3"],
                "actionItems": ["액션1", "액션2", "액션3"]
            }
            
            - summary: 이번 기간 활동을 한 줄로 요약
            - insights: 데이터에서 발견한 3가지 핵심 인사이트
            - actionItems: 다음 기간에 실천할 3가지 구체적인 액션 아이템
        """.trimIndent()
    }
    
    private fun buildProcrastinationPrompt(todos: List<Todo>): String {
        val incompleteTodos = todos.filter { !it.isCompleted }
        val categoryStats = incompleteTodos.groupBy { it.category }.mapValues { it.value.size }
        
        // 시간대별 분석 (생성 시간 기준)
        val timeSlotStats = incompleteTodos.groupBy { 
            when (it.createdAt.hour) {
                in 6..11 -> "오전 (6-12시)"
                in 12..17 -> "오후 (12-18시)"
                in 18..21 -> "저녁 (18-22시)"
                else -> "밤 (22-6시)"
            }
        }.mapValues { it.value.size }
        
        return """
            당신은 생산성 코치입니다. 사용자의 미완료 Todo 패턴을 분석해주세요.
            
            ## 미완료 데이터
            - 총 미완료: ${incompleteTodos.size}개
            - 카테고리별: ${categoryStats.entries.joinToString(", ") { "${it.key}: ${it.value}개" }}
            - 시간대별: ${timeSlotStats.entries.joinToString(", ") { "${it.key}: ${it.value}개" }}
            
            ## 미완료 할 일 목록
            ${incompleteTodos.take(15).joinToString("\n") { "- ${it.title} (${it.category}, ${it.priority}, ${it.createdAt.hour}시 생성)" }}
            
            ## 응답 형식 (JSON)
            다음 JSON 형식으로만 응답해주세요. 다른 텍스트 없이 JSON만 출력하세요:
            {
                "frequentCategories": ["자주 미루는 카테고리1", "카테고리2"],
                "frequentTimeSlots": ["자주 미루는 시간대1", "시간대2"],
                "aiComment": "미루기 패턴에 대한 한 줄 코멘트 (50자 이내)"
            }
        """.trimIndent()
    }
    
    private suspend fun callGeminiApi(prompt: String): String {
        val requestBody = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            )
        )
        
        val response: HttpResponse = httpClient.post("$baseUrl?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(GeminiRequest.serializer(), requestBody))
        }
        
        val responseText = response.body<String>()
        return extractTextFromResponse(responseText)
    }
    
    private fun extractTextFromResponse(responseJson: String): String {
        return try {
            val json = Json { ignoreUnknownKeys = true }
            val response = json.decodeFromString<GeminiResponse>(responseJson)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    private fun parseReportResponse(response: String): AIReport {
        return try {
            // JSON 블록 추출 (```json ... ``` 형식 처리)
            val jsonString = extractJsonFromResponse(response)
            val json = Json { ignoreUnknownKeys = true }
            val parsed = json.decodeFromString<ReportJson>(jsonString)
            AIReport(
                summary = parsed.summary,
                insights = parsed.insights,
                actionItems = parsed.actionItems
            )
        } catch (e: Exception) {
            AIReport(
                summary = "분석 결과를 파싱하는 중 오류가 발생했습니다.",
                insights = emptyList(),
                actionItems = emptyList()
            )
        }
    }
    
    private fun parseProcrastinationResponse(response: String): ProcrastinationPatterns {
        return try {
            val jsonString = extractJsonFromResponse(response)
            val json = Json { ignoreUnknownKeys = true }
            val parsed = json.decodeFromString<ProcrastinationJson>(jsonString)
            ProcrastinationPatterns(
                frequentCategories = parsed.frequentCategories,
                frequentTimeSlots = parsed.frequentTimeSlots,
                aiComment = parsed.aiComment
            )
        } catch (e: Exception) {
            ProcrastinationPatterns(
                frequentCategories = emptyList(),
                frequentTimeSlots = emptyList(),
                aiComment = "패턴 분석 중 오류가 발생했습니다."
            )
        }
    }
    
    private fun extractJsonFromResponse(response: String): String {
        // ```json ... ``` 블록 추출
        val jsonBlockRegex = "```json\\s*([\\s\\S]*?)\\s*```".toRegex()
        val match = jsonBlockRegex.find(response)
        if (match != null) {
            return match.groupValues[1].trim()
        }
        
        // { } 블록 추출
        val startIndex = response.indexOf('{')
        val endIndex = response.lastIndexOf('}')
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1)
        }
        
        return response
    }
}

// Gemini API Request/Response 모델
@Serializable
private data class GeminiRequest(
    val contents: List<Content>
)

@Serializable
private data class Content(
    val parts: List<Part>
)

@Serializable
private data class Part(
    val text: String
)

@Serializable
private data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
private data class Candidate(
    val content: ContentResponse? = null
)

@Serializable
private data class ContentResponse(
    val parts: List<PartResponse>? = null
)

@Serializable
private data class PartResponse(
    val text: String? = null
)

// 파싱용 모델
@Serializable
private data class ReportJson(
    val summary: String = "",
    val insights: List<String> = emptyList(),
    val actionItems: List<String> = emptyList()
)

@Serializable
private data class ProcrastinationJson(
    val frequentCategories: List<String> = emptyList(),
    val frequentTimeSlots: List<String> = emptyList(),
    val aiComment: String = ""
)
