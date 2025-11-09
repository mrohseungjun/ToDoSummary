package com.example.todosummer.core.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.example.todosummer.core.data.local.dao.TodoDao
import com.example.todosummer.core.data.local.dao.CategoryDao
import com.example.todosummer.core.data.local.entity.TodoEntity
import com.example.todosummer.core.data.local.entity.CategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Todo 앱의 Room 데이터베이스
 * Multiplatform 지원을 위한 설정 포함
 */
@ConstructedBy(TodoDatabaseConstructor::class) // Non-Android 타겟에서 필요
@Database(
    entities = [TodoEntity::class, CategoryEntity::class],
    version = 3,
    exportSchema = true
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun categoryDao(): CategoryDao
    
    companion object {
        const val DATABASE_NAME = "todo_database.db"
        
        /**
         * 버전 1 -> 2 마이그레이션
         * description, dueDate 제거 및 category 추가
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(connection: SQLiteConnection) {
                // 1. 새 테이블 생성
                connection.execSQL("""
                    CREATE TABLE IF NOT EXISTS todos_new (
                        id TEXT PRIMARY KEY NOT NULL,
                        title TEXT NOT NULL,
                        isCompleted INTEGER NOT NULL,
                        priority TEXT NOT NULL,
                        createdAt TEXT NOT NULL,
                        updatedAt TEXT,
                        category TEXT NOT NULL DEFAULT '업무'
                    )
                """.trimIndent())
                
                // 2. 기존 데이터 복사 (description, dueDate 제외)
                connection.execSQL("""
                    INSERT INTO todos_new (id, title, isCompleted, priority, createdAt, updatedAt, category)
                    SELECT id, title, isCompleted, priority, createdAt, updatedAt, '업무'
                    FROM todos
                """.trimIndent())
                
                // 3. 기존 테이블 삭제
                connection.execSQL("DROP TABLE todos")
                
                // 4. 새 테이블 이름 변경
                connection.execSQL("ALTER TABLE todos_new RENAME TO todos")
            }
        }
        
        /**
         * 버전 2 -> 3 마이그레이션
         * categories 테이블 추가 및 기본 카테고리 삽입
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(connection: SQLiteConnection) {
                // 1. categories 테이블 생성
                connection.execSQL("""
                    CREATE TABLE IF NOT EXISTS categories (
                        id TEXT PRIMARY KEY NOT NULL,
                        name TEXT NOT NULL,
                        createdAt TEXT NOT NULL
                    )
                """.trimIndent())
                
                // 2. 기본 카테고리 삽입
                val now = kotlinx.datetime.Clock.System.now()
                    .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                    .toString()
                
                connection.execSQL("""
                    INSERT INTO categories (id, name, createdAt) VALUES 
                    ('category_work', '업무', '$now'),
                    ('category_personal', '개인', '$now'),
                    ('category_exercise', '운동', '$now'),
                    ('category_study', '공부', '$now')
                """.trimIndent())
            }
        }
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
        .addMigrations(
            TodoDatabase.MIGRATION_1_2,
            TodoDatabase.MIGRATION_2_3
        )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
