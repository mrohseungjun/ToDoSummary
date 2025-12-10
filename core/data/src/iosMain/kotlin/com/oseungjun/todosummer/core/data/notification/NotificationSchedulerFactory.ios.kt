package com.oseungjun.todosummer.core.data.notification

import com.oseungjun.todosummer.core.domain.notification.NotificationScheduler

/**
 * iOS NotificationScheduler 생성
 */
actual fun createNotificationScheduler(): NotificationScheduler {
    return IOSNotificationScheduler()
}
