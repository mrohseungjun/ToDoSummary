package com.example.todosummer.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * 플랫폼별 DataStore 생성 함수
 */
expect fun createDataStore(): DataStore<Preferences>
