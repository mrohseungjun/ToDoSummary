package com.oseungjun.todosummer.core.data.notification

import android.app.NotificationManager
import android.app.PendingIntent
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
        println("[NotificationReceiver] onReceive called!")
        
        val todoId = intent.getStringExtra("TODO_ID") ?: run {
            println("[NotificationReceiver] ERROR: TODO_ID is null")
            return
        }
        val todoTitle = intent.getStringExtra("TODO_TITLE") ?: "할 일"
        val todoCategory = intent.getStringExtra("TODO_CATEGORY") ?: ""

        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // 알림 클릭 시 앱으로 이동하는 Intent
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("TODO_ID", todoId)
        }
        
        val contentIntent = PendingIntent.getActivity(
            context,
            todoId.hashCode(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
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
            .setContentIntent(contentIntent)  // 클릭 시 앱 실행
            .build()
        
        notificationManager.notify(todoId.hashCode(), notification)
    }
}
