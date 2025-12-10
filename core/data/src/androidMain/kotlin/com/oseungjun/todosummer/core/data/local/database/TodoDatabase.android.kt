package com.oseungjun.todosummer.core.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import org.koin.core.context.GlobalContext

/**
 * Android에서 Room 데이터베이스 생성
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<TodoDatabase> {
    val context = resolveApplicationContext()
    val dbFile = context.getDatabasePath(TodoDatabase.DATABASE_NAME)
    return Room.databaseBuilder<TodoDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}

private fun resolveApplicationContext(): Context {
    // Koin이 시작된 후 androidContext(context)를 등록했다고 가정하고 가져옵니다.
    val koin = GlobalContext.getOrNull()
        ?: throw IllegalStateException("Koin is not started. Call initKoinAndroid(context) before creating database.")
    return koin.get<Context>()
}
