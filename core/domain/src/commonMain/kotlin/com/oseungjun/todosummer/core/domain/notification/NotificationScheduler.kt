package com.oseungjun.todosummer.core.domain.notification

import com.oseungjun.todosummer.core.domain.model.Todo

/**
 * 플랫폼별 알림 스케줄링 인터페이스
 */
interface NotificationScheduler {
    /**
     * Todo 알림을 스케줄링합니다
     * @param todo 알림을 설정할 Todo 항목
     * @return 스케줄링 성공 여부
     */
    suspend fun scheduleNotification(todo: Todo): Boolean
    
    /**
     * Todo 알림을 취소합니다
     * @param todoId 알림을 취소할 Todo ID
     */
    suspend fun cancelNotification(todoId: String)
    
    /**
     * 모든 알림을 취소합니다
     */
    suspend fun cancelAllNotifications()
    
    /**
     * 알림 권한이 있는지 확인합니다
     * @return 권한 여부
     */
    suspend fun hasNotificationPermission(): Boolean
    
    /**
     * 알림 권한을 요청합니다
     * @return 권한 승인 여부
     */
    suspend fun requestNotificationPermission(): Boolean
}
