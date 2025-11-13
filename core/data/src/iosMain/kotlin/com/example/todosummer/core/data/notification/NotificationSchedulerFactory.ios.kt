package com.example.todosummer.core.data.notification

import com.example.todosummer.core.domain.notification.NotificationScheduler

/**
 * iOS NotificationScheduler 생성
 */
actual fun createNotificationScheduler(): NotificationScheduler {
    return IOSNotificationScheduler()
}
