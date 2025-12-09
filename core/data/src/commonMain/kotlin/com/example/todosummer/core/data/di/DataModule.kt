package com.example.todosummer.core.data.di

import com.example.todosummer.core.data.ai.GeminiServiceImpl
import com.example.todosummer.core.data.ai.createHttpClient
import com.example.todosummer.core.data.local.database.TodoDatabase
import com.example.todosummer.core.data.local.dao.TodoDao
import com.example.todosummer.core.data.local.dao.CategoryDao
import com.example.todosummer.core.data.repository.TodoRepositoryImpl
import com.example.todosummer.core.data.repository.CategoryRepositoryImpl
import com.example.todosummer.core.data.source.TodoDataSource
import com.example.todosummer.core.data.preferences.UserPreferencesRepository
import com.example.todosummer.core.data.preferences.createDataStore
import com.example.todosummer.core.data.notification.createNotificationScheduler
import com.example.todosummer.core.domain.ai.GeminiService
import com.example.todosummer.core.domain.repository.TodoRepository
import com.example.todosummer.core.domain.repository.CategoryRepository
import com.example.todosummer.core.domain.notification.NotificationScheduler
import org.koin.dsl.module

val dataModule = module {
    // Database & DAO
    single<TodoDatabase> { com.example.todosummer.core.data.local.database.createTodoDatabase() }
    single<TodoDao> { get<TodoDatabase>().todoDao() }
    single<CategoryDao> { get<TodoDatabase>().categoryDao() }

    // DataSource (Room-backed)
    single<TodoDataSource> { com.example.todosummer.core.data.source.RoomTodoDataSource(get()) }

    // Repository
    single<TodoRepository> { TodoRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    
    // DataStore & Preferences
    single { createDataStore() }
    single { UserPreferencesRepository(get()) }
    
    // Notification
    single<NotificationScheduler> { createNotificationScheduler() }
    
    // Gemini AI Service
    single { createHttpClient() }
    single<GeminiService> { 
        GeminiServiceImpl(
            httpClient = get(),
            apiKey = getGeminiApiKey()
        )
    }
}

/**
 * Gemini API 키를 가져옵니다.
 * 실제 앱에서는 BuildConfig나 환경 변수에서 가져와야 합니다.
 * TODO: 실제 API 키로 교체하세요
 */
expect fun getGeminiApiKey(): String
