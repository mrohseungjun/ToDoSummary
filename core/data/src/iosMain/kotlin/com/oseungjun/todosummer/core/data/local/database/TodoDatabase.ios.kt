package com.oseungjun.todosummer.core.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSHomeDirectory

/**
 * iOS에서 Room 데이터베이스 생성
 */
actual fun getDatabaseBuilder(): RoomDatabase.Builder<TodoDatabase> {
    val dbFilePath = documentDirectory() + "/${TodoDatabase.DATABASE_NAME}"
    return Room.databaseBuilder<TodoDatabase>(
        name = dbFilePath
    ).setDriver(BundledSQLiteDriver())
}

/**
 * iOS 문서 디렉토리 경로 가져오기
 */
@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    return NSHomeDirectory() + "/Documents"
}
