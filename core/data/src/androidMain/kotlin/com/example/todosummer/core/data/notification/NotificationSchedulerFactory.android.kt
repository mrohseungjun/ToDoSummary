package com.example.todosummer.core.data.notification

import android.content.Context
import com.example.todosummer.core.domain.notification.NotificationScheduler

private lateinit var appContext: Context

/**
 * Android Context 초기화
 */
fun initializeNotificationScheduler(context: Context) {
    appContext = context.applicationContext
}

/**
 * Android NotificationScheduler 생성
 */
actual fun createNotificationScheduler(): NotificationScheduler {
    return AndroidNotificationScheduler(appContext)
}
