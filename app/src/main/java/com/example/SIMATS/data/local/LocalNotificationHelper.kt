package com.example.SIMATS.data.local

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.SIMATS.MainActivity
import com.example.SIMATS.R
import com.example.SIMATS.data.model.NotificationItem
import com.example.SIMATS.data.model.UserRole

object LocalNotificationHelper {
    const val CHANNEL_ID_LEGACY = "momora_alerts"
    const val CHANNEL_ID_HEADS_UP = "momora_heads_up_alerts"
    const val CHANNEL_ID = CHANNEL_ID_HEADS_UP
    const val CHANNEL_NAME_LEGACY = "Momora Standard Alerts"
    const val CHANNEL_NAME_HEADS_UP = "Momora Priority Alerts"
    const val CHANNEL_NAME = CHANNEL_NAME_HEADS_UP
    const val NOTIFICATION_ID_ADMIN_ALERT = 1001

    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) {
        val app = context.applicationContext
        appContext = app
        createNotificationChannel(app)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .build()

            // 1. Preserve existing legacy channel without deletion for backward compatibility
            val legacyChannel = NotificationChannel(
                CHANNEL_ID_LEGACY,
                CHANNEL_NAME_LEGACY,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Momora AI alerts and updates (standard)"
            }
            manager.createNotificationChannel(legacyChannel)

            // 2. New dedicated HIGH importance channel for pop-up heads-up banners
            val headsUpChannel = NotificationChannel(
                CHANNEL_ID_HEADS_UP,
                CHANNEL_NAME_HEADS_UP,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Momora AI real-time heads-up alerts and notifications"
                enableLights(true)
                lightColor = android.graphics.Color.argb(255, 124, 77, 255)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setShowBadge(true)
            }
            manager.createNotificationChannel(headsUpChannel)
        }
    }

    fun postAdminAlertIfNeeded(notifications: List<NotificationItem>) {
        val context = appContext ?: return
        if (SessionManager.getUserRole() != UserRole.ADMIN) return
        if (!SessionManager.isAdminNotificationsEnabled()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!permissionGranted) return
        }

        val unreadItems = notifications.filter { !it.isRead }
        if (unreadItems.isEmpty()) return

        val lastShownId = SessionManager.getLastShownNotificationId()
        val candidateItems = unreadItems.filter { item ->
            val numId = item.id.toIntOrNull() ?: 0
            numId > lastShownId
        }

        if (candidateItems.isEmpty()) return

        val maxId = candidateItems.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: return
        val newestItem = candidateItems.firstOrNull { (it.id.toIntOrNull() ?: 0) == maxId } ?: candidateItems.first()

        SessionManager.setLastShownNotificationId(maxId)

        val displayText = newestItem.title.ifBlank { newestItem.subtitle }.ifBlank { "New notification received" }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val vibrationPattern = longArrayOf(0, 250, 150, 250)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle("Momora AI")
            .setContentText(displayText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(displayText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSound(soundUri)
            .setVibrate(vibrationPattern)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID_ADMIN_ALERT, notification)
        } catch (_: SecurityException) {
            // Permission revoked
        } catch (_: Exception) {
            // Safe fallback
        }
    }

    fun openAppNotificationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(fallbackIntent)
            } catch (_: Exception) {}
        }
    }
}
