package com.example.SIMATS.data.local

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.SIMATS.data.model.MedicationItem
import com.example.SIMATS.data.receiver.MedicationReminderReceiver
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

object MedicationNotificationScheduler {
    const val CHANNEL_ID_MEDICATION = "momora_medication_reminders"
    const val CHANNEL_NAME_MEDICATION = "Prescribed Medication Reminders"
    const val ACTION_MEDICATION_REMINDER = "com.example.SIMATS.ACTION_MEDICATION_REMINDER"

    const val EXTRA_MEDICATION_ID = "extra_medication_id"
    const val EXTRA_MEDICATION_NAME = "extra_medication_name"
    const val EXTRA_DOSAGE_AND_TIME = "extra_dosage_and_time"
    const val EXTRA_DOCTOR_NOTE = "extra_doctor_note"
    const val EXTRA_REMINDER_TIME = "extra_reminder_time"

    private const val PREF_MED_CACHE = "momora_medication_alarm_cache"
    private const val KEY_CACHED_MEDS = "cached_active_medications"

    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        createNotificationChannel(context)
    }

    fun getContext(): Context? = appContext

    fun createNotificationChannel(context: Context? = null) {
        val ctx = context ?: appContext ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID_MEDICATION,
                CHANNEL_NAME_MEDICATION,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily medication dose reminders for pregnant patients"
                enableLights(true)
                lightColor = android.graphics.Color.argb(255, 124, 77, 255)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setShowBadge(true)
            }
            manager.createNotificationChannel(channel)
        }
    }

    fun isNotificationPermissionGranted(context: Context? = null): Boolean {
        val ctx = context ?: appContext ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                ctx,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun scheduleReminder(context: Context? = null, item: MedicationItem) {
        val ctx = context ?: appContext ?: return
        if (!SessionManager.getReminderMedication()) return

        val reminderTimeStr = item.reminderTime?.trim() ?: "08:00"
        val timeParts = reminderTimeStr.split(":")
        val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(ctx, MedicationReminderReceiver::class.java).apply {
            action = ACTION_MEDICATION_REMINDER
            putExtra(EXTRA_MEDICATION_ID, item.id)
            putExtra(EXTRA_MEDICATION_NAME, item.name)
            putExtra(EXTRA_DOSAGE_AND_TIME, item.dosageAndTime)
            putExtra(EXTRA_DOCTOR_NOTE, item.doctorPrescriptionNote)
            putExtra(EXTRA_REMINDER_TIME, reminderTimeStr)
        }

        val requestCode = item.id * 100
        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            } else {
                alarmManager?.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            }
        } catch (_: SecurityException) {
            // In case exact alarm permission isn't granted, fallback to inexact
            alarmManager?.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
        }
    }

    fun scheduleNextDay(
        context: Context? = null,
        medicationId: Int,
        name: String,
        dosageAndTime: String,
        doctorNote: String?,
        reminderTimeStr: String
    ) {
        val ctx = context ?: appContext ?: return
        val timeParts = reminderTimeStr.split(":")
        val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val intent = Intent(ctx, MedicationReminderReceiver::class.java).apply {
            action = ACTION_MEDICATION_REMINDER
            putExtra(EXTRA_MEDICATION_ID, medicationId)
            putExtra(EXTRA_MEDICATION_NAME, name)
            putExtra(EXTRA_DOSAGE_AND_TIME, dosageAndTime)
            putExtra(EXTRA_DOCTOR_NOTE, doctorNote)
            putExtra(EXTRA_REMINDER_TIME, reminderTimeStr)
        }

        val requestCode = medicationId * 100
        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            } else {
                alarmManager?.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            }
        } catch (_: Exception) {
            alarmManager?.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
        }
    }

    fun cancelReminder(context: Context? = null, medicationId: Int) {
        val ctx = context ?: appContext ?: return
        val intent = Intent(ctx, MedicationReminderReceiver::class.java).apply {
            action = ACTION_MEDICATION_REMINDER
        }
        val requestCode = medicationId * 100
        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun cancelAll(context: Context? = null) {
        val ctx = context ?: appContext ?: return
        val cached = getCachedMedications(ctx)
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        cached.forEach { item ->
            val requestCode = item.id * 100
            val intent = Intent(ctx, MedicationReminderReceiver::class.java).apply {
                action = ACTION_MEDICATION_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager?.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
    }

    fun showImmediateNotification(context: Context? = null, item: MedicationItem) {
        val ctx = context ?: appContext ?: return
        if (!isNotificationPermissionGranted(ctx)) return

        createNotificationChannel(ctx)

        val launchIntent = Intent(ctx, com.example.SIMATS.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("extra_target_screen", "medication")
            putExtra("KEY_NAV_TAB", 3)
        }
        val pendingIntent = PendingIntent.getActivity(
            ctx,
            item.id * 100 + 1,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val vibrationPattern = longArrayOf(0, 300, 200, 300)

        val title = "Medication Reminder · Momora AI"
        val reminderTime = item.reminderTime ?: "Scheduled time"
        val shortText = "Time to take ${item.name} ($reminderTime)"
        val detailedText = buildString {
            append("Time to take ${item.name} ($reminderTime)\n")
            append("Prescribed dosage: ${item.dosageAndTime}\n")
            if (!item.doctorPrescriptionNote.isNullOrBlank()) {
                append(item.doctorPrescriptionNote)
            } else {
                append("Take as prescribed by your healthcare provider.")
            }
        }

        val notification = androidx.core.app.NotificationCompat.Builder(
            ctx,
            CHANNEL_ID_MEDICATION
        )
            .setSmallIcon(com.example.SIMATS.R.drawable.ic_bell)
            .setContentTitle(title)
            .setContentText(shortText)
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(detailedText))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setCategory(androidx.core.app.NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(soundUri)
            .setVibrate(vibrationPattern)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            androidx.core.app.NotificationManagerCompat.from(ctx).notify(item.id * 100 + 1, notification)
        } catch (_: Exception) {}
    }

    fun rescheduleAll(context: Context? = null, items: List<MedicationItem>) {
        val ctx = context ?: appContext ?: return
        cacheMedications(ctx, items)
        val enabled = SessionManager.getReminderMedication()
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        items.forEach { item ->
            val requestCode = item.id * 100
            val intent = Intent(ctx, MedicationReminderReceiver::class.java).apply {
                action = ACTION_MEDICATION_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager?.cancel(pendingIntent)
                pendingIntent.cancel()
            }

            if (enabled && item.isActive) {
                scheduleReminder(ctx, item)
            }
        }
    }

    fun cacheMedications(context: Context, items: List<MedicationItem>) {
        val prefs = context.getSharedPreferences(PREF_MED_CACHE, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        items.filter { it.isActive }.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("dosageAndTime", item.dosageAndTime)
                put("doctorPrescriptionNote", item.doctorPrescriptionNote ?: "")
                put("reminderTime", item.reminderTime ?: "08:00")
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_CACHED_MEDS, jsonArray.toString()).apply()
    }

    fun getCachedMedications(context: Context): List<MedicationItem> {
        val prefs = context.getSharedPreferences(PREF_MED_CACHE, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_CACHED_MEDS, null) ?: return emptyList()
        val list = mutableListOf<MedicationItem>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    MedicationItem(
                        id = obj.getInt("id"),
                        name = obj.getString("name"),
                        dosageAndTime = obj.getString("dosageAndTime"),
                        doctorPrescriptionNote = obj.optString("doctorPrescriptionNote").ifBlank { null },
                        reminderTime = obj.optString("reminderTime", "08:00"),
                        isTaken = false,
                        isActive = true
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
