package com.example.SIMATS.data.service

import android.app.ActivityManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.SIMATS.MainActivity
import com.example.SIMATS.R
import com.example.SIMATS.data.local.FcmTokenManager
import com.example.SIMATS.data.local.LocalNotificationHelper
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.UserRole
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MomoraFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MomoraFCMService"
        const val EXTRA_TARGET_SCREEN = "extra_target_screen"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_ROLE = "extra_role"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token received: $token")
        FcmTokenManager.saveToken(token)

        if (SessionManager.isLoggedIn()) {
            FcmTokenManager.registerTokenWithBackend(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        // 1. Extract payload data
        val data = remoteMessage.data
        val notification = remoteMessage.notification

        val title = notification?.title ?: data["title"] ?: "Momora AI"
        val subtitle = notification?.body ?: data["subtitle"] ?: data["body"] ?: ""
        val targetRole = data["role"]?.lowercase()?.trim()
        val targetScreen = data["screen"] ?: "notifications"
        val notificationIdStr = data["notification_id"] ?: System.currentTimeMillis().toString()

        // 2. Role Security Check: Never mix patient, doctor, and admin notifications
        if (SessionManager.isLoggedIn()) {
            val currentRole = SessionManager.getUserRole()
            if (!targetRole.isNullOrBlank()) {
                val matchesCurrentRole = when (currentRole) {
                    UserRole.PATIENT -> targetRole == "patient"
                    UserRole.DOCTOR -> targetRole == "doctor"
                    UserRole.ADMIN -> targetRole == "admin"
                }
                if (!matchesCurrentRole) {
                    Log.w(TAG, "Notification intended for role '$targetRole' ignored because logged in as '$currentRole'")
                    return
                }
            }
        } else {
            // Logged-out users should not receive personalized clinical or appointment notifications
            if (!targetRole.isNullOrBlank()) {
                Log.d(TAG, "Notification ignored because user is not currently authenticated")
                return
            }
        }

        // 3. Display heads-up banner via high-importance channel across all app states
        showHeadsUpNotification(
            title = title,
            subtitle = subtitle,
            targetScreen = targetScreen,
            targetRole = targetRole,
            notificationId = notificationIdStr
        )
    }

    private fun showHeadsUpNotification(
        title: String,
        subtitle: String,
        targetScreen: String,
        targetRole: String?,
        notificationId: String
    ) {
        val context = applicationContext
        LocalNotificationHelper.createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TARGET_SCREEN, targetScreen)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(EXTRA_ROLE, targetRole)
        }

        val requestCode = notificationId.hashCode()
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val vibrationPattern = longArrayOf(0, 250, 150, 250)

        val builder = NotificationCompat.Builder(context, LocalNotificationHelper.CHANNEL_ID_HEADS_UP)
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(subtitle))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(soundUri)
            .setVibrate(vibrationPattern)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notifManager = NotificationManagerCompat.from(context)
        try {
            notifManager.notify(requestCode, builder.build())
        } catch (e: SecurityException) {
            Log.e(TAG, "Missing notification permission to post notification: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to display notification banner: ${e.message}")
        }
    }

    private fun isAppInForeground(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return false
        val processes = am.runningAppProcesses ?: return false
        val pkg = packageName
        for (proc in processes) {
            if (proc.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                proc.processName == pkg
            ) {
                return true
            }
        }
        return false
    }
}
