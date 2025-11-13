package com.example.todosummer.core.data.notification

import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.domain.notification.NotificationScheduler
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import platform.UserNotifications.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * iOS 플랫폼 알림 스케줄러
 */
@OptIn(ExperimentalForeignApi::class)
class IOSNotificationScheduler : NotificationScheduler {
    
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    
    override suspend fun scheduleNotification(todo: Todo): Boolean {
        if (!todo.hasReminder) {
            return false
        }

        val reminderTime = todo.reminderTime ?: return false
        
        // 권한 확인
        if (!hasNotificationPermission()) {
            return false
        }
        
        try {
            val reminderTimeMillis = reminderTime.toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
            val currentMillis = Clock.System.now().toEpochMilliseconds()

            // 현재 시간보다 이전이면 스케줄링하지 않음
            if (reminderTimeMillis <= currentMillis) {
                return false
            }
            
            // 알림 콘텐츠 생성
            val content = UNMutableNotificationContent().apply {
                setTitle("📋 할 일 알림")
                setBody(todo.title)
                if (todo.category.isNotEmpty()) {
                    setSubtitle("카테고리: ${todo.category}")
                }
                setSound(UNNotificationSound.defaultSound())
            }
            
            // 트리거 시간 계산 (초 단위)
            val timeInterval = (reminderTimeMillis - currentMillis) / 1000.0
            val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                timeInterval = timeInterval,
                repeats = false
            )
            
            // 알림 요청 생성
            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = todo.id,
                content = content,
                trigger = trigger
            )
            
            // 알림 스케줄링
            return suspendCoroutine { continuation ->
                notificationCenter.addNotificationRequest(request) { error ->
                    continuation.resume(error == null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    override suspend fun cancelNotification(todoId: String) {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(todoId))
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(todoId))
    }
    
    override suspend fun cancelAllNotifications() {
        notificationCenter.removeAllPendingNotificationRequests()
        notificationCenter.removeAllDeliveredNotifications()
    }
    
    override suspend fun hasNotificationPermission(): Boolean {
        return suspendCoroutine { continuation ->
            notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
                val hasPermission = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
                continuation.resume(hasPermission)
            }
        }
    }
    
    override suspend fun requestNotificationPermission(): Boolean {
        return suspendCoroutine { continuation ->
            notificationCenter.requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
            ) { granted, error ->
                continuation.resume(granted && error == null)
            }
        }
    }
}
