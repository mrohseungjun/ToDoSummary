package com.example.todosummer.core.data.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todosummer.core.domain.model.Todo
import com.example.todosummer.core.domain.notification.NotificationScheduler
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * Android 플랫폼 알림 스케줄러
 */
class AndroidNotificationScheduler(
    private val context: Context
) : NotificationScheduler {
    
    companion object {
        private const val CHANNEL_ID = "todo_reminders"
        private const val CHANNEL_NAME = "할 일 알림"
        private const val CHANNEL_DESCRIPTION = "할 일 마감 알림"
    }
    
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    init {
        createNotificationChannel()
    }
    
    /**
     * 알림 채널 생성 (Android 8.0 이상)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override suspend fun scheduleNotification(todo: Todo): Boolean {
        if (!todo.hasReminder || todo.reminderTime == null) {
            return false
        }
        
        // 권한 확인
        if (!hasNotificationPermission()) {
            return false
        }
        
        try {
            val reminderTime = todo.reminderTime ?: return false
            val reminderTimeMillis = reminderTime.toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
            
            // 현재 시간보다 이전이면 스케줄링하지 않음
            if (reminderTimeMillis <= System.currentTimeMillis()) {
                return false
            }
            
            val intent = Intent(context, TodoNotificationReceiver::class.java).apply {
                putExtra("TODO_ID", todo.id)
                putExtra("TODO_TITLE", todo.title)
                putExtra("TODO_CATEGORY", todo.category)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                todo.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // AlarmManager로 정확한 시간에 알림 스케줄링
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminderTimeMillis,
                    pendingIntent
                )
            }
            
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    override suspend fun cancelNotification(todoId: String) {
        try {
            val intent = Intent(context, TodoNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                todoId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override suspend fun cancelAllNotifications() {
        // 개별 알림 취소는 TodoId가 필요하므로 여기서는 표시된 알림만 제거
        notificationManager.cancelAll()
    }
    
    override suspend fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Android 13 미만은 권한 불필요
        }
    }
    
    override suspend fun requestNotificationPermission(): Boolean {
        // Activity에서 권한 요청을 처리해야 하므로 여기서는 현재 권한 상태만 반환
        return hasNotificationPermission()
    }
}
