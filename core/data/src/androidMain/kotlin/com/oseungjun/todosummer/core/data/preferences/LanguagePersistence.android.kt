package com.oseungjun.todosummer.core.data.preferences

import com.oseungjun.todosummer.core.common.localization.LanguageMode
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import android.content.Context

/**
 * Android: SharedPreferences에 언어 설정 저장
 */
actual fun persistLanguageMode(mode: LanguageMode) {
    LanguagePersistenceHelper.persist(mode)
}

internal object LanguagePersistenceHelper : KoinComponent {
    private val context: Context by inject()
    
    fun persist(mode: LanguageMode) {
        LanguagePreferences.setLanguageMode(context, mode)
    }
}
