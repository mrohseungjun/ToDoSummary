package com.example.todosummer.core.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Android에서 Room 데이터베이스 생성
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<TodoDatabase> {
    val context = getApplicationContext()
    val dbFile = context.getDatabasePath(TodoDatabase.DATABASE_NAME)
    return Room.databaseBuilder<TodoDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}

/**
 * Android Context를 가져오는 함수
 * 실제 구현에서는 Application Context를 주입받아야 함
 */
private fun getApplicationContext(): Context {
    // 이 부분은 실제로는 DI를 통해 Context를 주입받아야 합니다
    // 지금은 임시로 예외를 던지고, 나중에 Koin을 통해 Context를 주입받도록 수정할 예정
    throw IllegalStateException("Application Context must be provided through DI")
}
