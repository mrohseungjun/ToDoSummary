package com.example.todosummer.core.data.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

/**
 * Todo 알림을 표시하는 BroadcastReceiver
 */
class TodoNotificationReceiver : BroadcastReceiver() {
    
    companion object {
        private const val CHANNEL_ID = "todo_reminders"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getStringExtra("TODO_ID") ?: return
        val todoTitle = intent.getStringExtra("TODO_TITLE") ?: "할 일"
        val todoCategory = intent.getStringExtra("TODO_CATEGORY") ?: ""
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: 앱 아이콘으로 변경
            .setContentTitle("📋 할 일 알림")
            .setContentText(todoTitle)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$todoTitle${if (todoCategory.isNotEmpty()) "\n카테고리: $todoCategory" else ""}")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()
        
        notificationManager.notify(todoId.hashCode(), notification)
    }
}
