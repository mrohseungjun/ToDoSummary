package com.oseungjun.todosummer.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

private lateinit var appContext: Context

/**
 * Android Context 초기화
 */
fun initializeDataStore(context: Context) {
    appContext = context.applicationContext
}

/**
 * Android DataStore 생성
 */
actual fun createDataStore(): DataStore<Preferences> {
    return appContext.dataStore
}
