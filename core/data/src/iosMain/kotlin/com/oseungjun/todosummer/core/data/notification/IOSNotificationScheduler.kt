package com.oseungjun.todosummer.core.data.notification

import com.oseungjun.todosummer.core.domain.model.Todo
import com.oseungjun.todosummer.core.domain.notification.NotificationScheduler
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
        println("[iOS Notification] scheduleNotification called for todo: ${todo.title}")
        println("[iOS Notification] hasReminder=${todo.hasReminder}, reminderTime=${todo.reminderTime}")
        
        if (!todo.hasReminder) {
            println("[iOS Notification] SKIP: hasReminder=false")
            return false
        }

        val reminderTime = todo.reminderTime ?: run {
            println("[iOS Notification] SKIP: reminderTime=null")
            return false
        }
        
        // 권한 확인
        val hasPermission = hasNotificationPermission()
        println("[iOS Notification] hasNotificationPermission=$hasPermission")
        if (!hasPermission) {
            println("[iOS Notification] SKIP: No notification permission")
            return false
        }
        
        try {
            val reminderTimeMillis = reminderTime.toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
            val currentMillis = Clock.System.now().toEpochMilliseconds()
            
            println("[iOS Notification] reminderTimeMillis=$reminderTimeMillis")
            println("[iOS Notification] currentMillis=$currentMillis")
            println("[iOS Notification] diff=${reminderTimeMillis - currentMillis}ms (${(reminderTimeMillis - currentMillis) / 1000}s)")

            // 현재 시간보다 이전이면 스케줄링하지 않음
            if (reminderTimeMillis <= currentMillis) {
                println("[iOS Notification] SKIP: reminderTime is in the past!")
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
            println("[iOS Notification] timeInterval=${timeInterval}s")
            
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
                    if (error != null) {
                        println("[iOS Notification] ERROR: ${error.localizedDescription}")
                        continuation.resume(false)
                    } else {
                        println("[iOS Notification] SUCCESS: Notification scheduled in ${timeInterval}s")
                        continuation.resume(true)
                    }
                }
            }
        } catch (e: Exception) {
            println("[iOS Notification] ERROR: ${e.message}")
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
