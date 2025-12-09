package com.example.todosummer.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.todosummer.core.common.localization.LanguageMode
import com.example.todosummer.core.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 사용자 설정을 저장하고 불러오는 Repository
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val LANGUAGE_MODE_KEY = stringPreferencesKey("language_mode")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
    
    /**
     * 언어 모드 Flow
     */
    val languageMode: Flow<LanguageMode> = dataStore.data.map { preferences ->
        val languageString = preferences[LANGUAGE_MODE_KEY] ?: LanguageMode.KOREAN.name
        try {
            LanguageMode.valueOf(languageString)
        } catch (e: IllegalArgumentException) {
            LanguageMode.KOREAN
        }
    }
    
    /**
     * 테마 모드 Flow
     */
    val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        val themeString = preferences[THEME_MODE_KEY] ?: ThemeMode.DARK.name
        try {
            ThemeMode.valueOf(themeString)
        } catch (e: IllegalArgumentException) {
            ThemeMode.DARK
        }
    }
    
    /**
     * 언어 모드 저장
     */
    suspend fun setLanguageMode(mode: LanguageMode) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_MODE_KEY] = mode.name
        }
        // Android: SharedPreferences에도 저장 (attachBaseContext에서 사용)
        persistLanguageMode(mode)
    }
    
    /**
     * 테마 모드 저장
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }
}
