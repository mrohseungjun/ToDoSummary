package com.example.todosummer.core.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.todosummer.core.data.local.dao.TodoDao
import com.example.todosummer.core.data.local.entity.TodoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Todo 앱의 Room 데이터베이스
 * Multiplatform 지원을 위한 설정 포함
 */
@ConstructedBy(TodoDatabaseConstructor::class) // Non-Android 타겟에서 필요
@Database(
    entities = [TodoEntity::class],
    version = 1,
    exportSchema = true
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    
    companion object {
        const val DATABASE_NAME = "todo_database.db"
    }
}

// Room 컴파일러가 각 타겟 소스셋에 actual 구현을 생성합니다.
@Suppress("KotlinNoActualForExpect")
expect object TodoDatabaseConstructor : RoomDatabaseConstructor<TodoDatabase> {
    override fun initialize(): TodoDatabase
}

/**
 * 플랫폼별 데이터베이스 인스턴스 생성을 위한 expect 함수
 */
expect fun getDatabaseBuilder(): RoomDatabase.Builder<TodoDatabase>

/**
 * 공통 데이터베이스 생성 함수
 * 플랫폼에 관계없이 동일한 설정 적용
 */
fun createTodoDatabase(): TodoDatabase {
    return getDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
