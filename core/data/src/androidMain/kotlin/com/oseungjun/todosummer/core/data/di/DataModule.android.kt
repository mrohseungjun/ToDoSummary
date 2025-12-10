package com.oseungjun.todosummer.core.data.di

import com.oseungjun.todosummer.core.data.BuildConfig

/**
 * Android에서 Gemini API 키를 가져옵니다.
 * local.properties 의 GEMINI_API_KEY 값을 BuildConfig 를 통해 노출합니다.
 */
actual fun getGeminiApiKey(): String {
    return BuildConfig.GEMINI_API_KEY
}
