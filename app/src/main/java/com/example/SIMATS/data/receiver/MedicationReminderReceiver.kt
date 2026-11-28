package com.example.SIMATS.data.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.SIMATS.MainActivity
import com.example.SIMATS.R
import com.example.SIMATS.data.local.MedicationNotificationScheduler
import com.example.SIMATS.data.local.SessionManager

class MedicationReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        SessionManager.init(context)
        MedicationNotificationScheduler.init(context)

        val action = intent.action

        // 1. Restore scheduled alarms after device reboot or app update
        if (action == Intent.ACTION_BOOT_COMPLETED || action == "android.intent.action.MY_PACKAGE_REPLACED") {
            val cachedMeds = MedicationNotificationScheduler.getCachedMedications(context)
            if (cachedMeds.isNotEmpty()) {
                MedicationNotificationScheduler.rescheduleAll(context, cachedMeds)
            }
            return
        }

        // 2. Handle firing of a scheduled medication alarm
        if (action == MedicationNotificationScheduler.ACTION_MEDICATION_REMINDER) {
            if (!SessionManager.getReminderMedication()) {
                return
            }

            val medId = intent.getIntExtra(MedicationNotificationScheduler.EXTRA_MEDICATION_ID, 0)
            val medName = intent.getStringExtra(MedicationNotificationScheduler.EXTRA_MEDICATION_NAME) ?: "Prescribed Medication"
            val dosageAndTime = intent.getStringExtra(MedicationNotificationScheduler.EXTRA_DOSAGE_AND_TIME) ?: "Take as scheduled"
            val doctorNote = intent.getStringExtra(MedicationNotificationScheduler.EXTRA_DOCTOR_NOTE)
            val reminderTime = intent.getStringExtra(MedicationNotificationScheduler.EXTRA_REMINDER_TIME) ?: "Scheduled time"

            if (!MedicationNotificationScheduler.isNotificationPermissionGranted(context)) {
                return
            }

            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("extra_target_screen", "medication")
                putExtra("KEY_NAV_TAB", 3)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                medId * 100,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val vibrationPattern = longArrayOf(0, 300, 200, 300)

            val title = "Medication Reminder · Momora AI"
            val shortText = "Time to take $medName ($reminderTime)"
            val detailedText = buildString {
                append("Time to take $medName ($reminderTime)\n")
                append("Prescribed dosage: $dosageAndTime\n")
                if (!doctorNote.isNullOrBlank()) {
                    append(doctorNote)
                } else {
                    append("Take as prescribed by your healthcare provider.")
                }
            }

            val notification = NotificationCompat.Builder(
                context,
                MedicationNotificationScheduler.CHANNEL_ID_MEDICATION
            )
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle(title)
                .setContentText(shortText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(detailedText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(soundUri)
                .setVibrate(vibrationPattern)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            try {
                val notificationManager = NotificationManagerCompat.from(context)
                notificationManager.notify(medId * 100, notification)
            } catch (_: SecurityException) {
                // Permission revoked
            } catch (_: Exception) {
                // Fallback
            }

            // Reschedule repeating alarm for next day
            if (medId > 0) {
                MedicationNotificationScheduler.scheduleNextDay(
                    context = context,
                    medicationId = medId,
                    name = medName,
                    dosageAndTime = dosageAndTime,
                    doctorNote = doctorNote,
                    reminderTimeStr = reminderTime
                )
            }
        }
    }
}
