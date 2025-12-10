package com.oseungjun.todosummer.core.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.oseungjun.todosummer.core.common.localization.LanguageMode
import java.util.Locale

/**
 * 언어 설정을 SharedPreferences로 동기적으로 관리
 * attachBaseContext에서 사용하기 위함
 */
object LanguagePreferences {
    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "language_mode"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * 언어 모드 저장
     */
    fun setLanguageMode(context: Context, mode: LanguageMode) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, mode.name).apply()
    }
    
    /**
     * 언어 모드 읽기 (동기)
     */
    fun getLanguageMode(context: Context): LanguageMode {
        val value = getPrefs(context).getString(KEY_LANGUAGE, LanguageMode.KOREAN.name)
        return try {
            LanguageMode.valueOf(value ?: LanguageMode.KOREAN.name)
        } catch (e: IllegalArgumentException) {
            LanguageMode.KOREAN
        }
    }
    
    /**
     * 언어 모드에 따른 Locale 반환
     */
    fun getLocale(context: Context): Locale {
        return when (getLanguageMode(context)) {
            LanguageMode.KOREAN -> Locale.KOREAN
            LanguageMode.ENGLISH -> Locale.ENGLISH
        }
    }
}
