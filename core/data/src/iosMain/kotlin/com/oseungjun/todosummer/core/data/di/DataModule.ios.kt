package com.oseungjun.todosummer.core.data.di

import platform.Foundation.NSBundle

/**
 * iOS에서 Gemini API 키를 가져옵니다.
 * Info.plist 의 GEMINI_API_KEY 값을 읽어 사용합니다.
 */
actual fun getGeminiApiKey(): String {
    val bundle = NSBundle.mainBundle
    val value = bundle.objectForInfoDictionaryKey("GEMINI_API_KEY") as? String
    return value ?: ""
}
