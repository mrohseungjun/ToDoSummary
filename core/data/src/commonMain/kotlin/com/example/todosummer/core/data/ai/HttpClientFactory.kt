package com.example.todosummer.core.data.ai

import io.ktor.client.*

/**
 * 플랫폼별 HTTP 클라이언트 생성
 */
expect fun createHttpClient(): HttpClient
