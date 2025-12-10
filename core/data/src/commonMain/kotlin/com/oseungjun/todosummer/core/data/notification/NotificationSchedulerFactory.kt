package com.oseungjun.todosummer.core.data.notification

import com.oseungjun.todosummer.core.domain.notification.NotificationScheduler

/**
 * 플랫폼별 NotificationScheduler 생성 함수
 */
expect fun createNotificationScheduler(): NotificationScheduler
