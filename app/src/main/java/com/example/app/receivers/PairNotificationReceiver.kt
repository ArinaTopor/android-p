package com.example.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.profile.domain.NotificationConstants

class PairNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val userName = intent.getStringExtra(NotificationConstants.EXTRA_USER_NAME) ?: "Пользователь"
        val mainActivityClass = intent.getStringExtra(NotificationConstants.EXTRA_MAIN_ACTIVITY_CLASS)
        val notificationManager = NotificationManagerCompat.from(context)
        
        if (!notificationManager.areNotificationsEnabled()) {
            return
        }
        
        val channel = notificationManager.getNotificationChannel(NotificationConstants.CHANNEL_ID)
        if (channel == null) {
            return
        }
        
        val mainActivityIntent = if (mainActivityClass != null) {
            try {
                val activityClass = Class.forName(mainActivityClass)
                Intent(context, activityClass).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            } catch (e: ClassNotFoundException) {
                null
            }
        } else {
            null
        }
        
        val notification = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Начало пары")
            .setContentText("У $userName начинается пара!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .apply {
                if (mainActivityIntent != null) {
                    setContentIntent(
                        android.app.PendingIntent.getActivity(
                            context,
                            0,
                            mainActivityIntent,
                            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                }
            }
            .build()
        
        try {
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    companion object {
        const val NOTIFICATION_ID = 1
    }
}

