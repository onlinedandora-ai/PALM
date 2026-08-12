package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

/**
 * PALM Alarm Scheduler
 *
 * Schedules a pair of exact alarms for each notification event:
 *   1. A **trigger alarm** at [triggerAtMillis]  → fires [PalmNotificationReceiver]
 *      which posts the notification.
 *   2. A **dismiss alarm**  at [deadlineMillis]  → fires [PalmNotificationDismissReceiver]
 *      which cancels the notification so stale alerts never linger past their deadline.
 *
 * Uses [AlarmManager.setExactAndAllowWhileIdle] for exact delivery on Android 6+
 * (including Doze mode). Gracefully falls back to [AlarmManager.set] if the
 * SCHEDULE_EXACT_ALARM permission is not granted (Android 12+ strict mode).
 */
object PalmAlarmScheduler {

    private const val TAG = "PalmAlarmScheduler"

    // Request-code offsets so trigger and dismiss PendingIntents are distinct
    private const val RC_TRIGGER_OFFSET = 10_000
    private const val RC_DISMISS_OFFSET = 20_000

    /**
     * Schedule a notification that fires at [triggerAtMillis] and is
     * automatically dismissed at [deadlineMillis].
     *
     * @param notificationId  Stable ID (used both as PendingIntent request code
     *                        and as the system notification ID).
     * @param triggerAtMillis Wall-clock epoch millis when the notification should appear.
     * @param deadlineMillis  Wall-clock epoch millis when the notification should
     *                        auto-cancel (the "correct end time").
     * @param title           Notification title string.
     * @param message         Notification body string.
     * @param channelId       One of [PalmNotificationManager.CHANNEL_RENEWALS] etc.
     */
    fun scheduleNotification(
        context: Context,
        notificationId: Int,
        triggerAtMillis: Long,
        deadlineMillis: Long,
        title: String,
        message: String,
        channelId: String = PalmNotificationManager.CHANNEL_RENEWALS
    ) {
        val now = System.currentTimeMillis()

        // ── Trigger alarm ─────────────────────────────────────────────────
        if (triggerAtMillis > now) {
            val triggerIntent = buildIntent(
                context,
                PalmNotificationReceiver::class.java,
                notificationId,
                title,
                message,
                channelId
            )
            val triggerPending = PendingIntent.getBroadcast(
                context,
                RC_TRIGGER_OFFSET + notificationId,
                triggerIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setExactAlarm(context, triggerAtMillis, triggerPending)
            Log.d(TAG, "Scheduled trigger alarm id=$notificationId at $triggerAtMillis")
        }

        // ── Dismiss alarm ─────────────────────────────────────────────────
        if (deadlineMillis > now) {
            val dismissIntent = Intent(context, PalmNotificationDismissReceiver::class.java).apply {
                putExtra(PalmNotificationManager.EXTRA_NOTIFICATION_ID, notificationId)
            }
            val dismissPending = PendingIntent.getBroadcast(
                context,
                RC_DISMISS_OFFSET + notificationId,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setExactAlarm(context, deadlineMillis, dismissPending)
            Log.d(TAG, "Scheduled dismiss alarm id=$notificationId at $deadlineMillis")
        }
    }

    /**
     * Cancel both the trigger and dismiss alarms for a given [notificationId].
     * Call this when the user has manually acknowledged a renewal.
     */
    fun cancelAlarms(context: Context, notificationId: Int) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel trigger
        val triggerIntent = Intent(context, PalmNotificationReceiver::class.java)
        val triggerPending = PendingIntent.getBroadcast(
            context,
            RC_TRIGGER_OFFSET + notificationId,
            triggerIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        triggerPending?.let { am.cancel(it); it.cancel() }

        // Cancel dismiss
        val dismissIntent = Intent(context, PalmNotificationDismissReceiver::class.java)
        val dismissPending = PendingIntent.getBroadcast(
            context,
            RC_DISMISS_OFFSET + notificationId,
            dismissIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        dismissPending?.let { am.cancel(it); it.cancel() }

        Log.d(TAG, "Cancelled alarms for id=$notificationId")
    }

    // ─────────────────────────── Helpers ─────────────────────────────────

    private fun <T> buildIntent(
        context: Context,
        receiverClass: Class<T>,
        notificationId: Int,
        title: String,
        message: String,
        channelId: String
    ): Intent = Intent(context, receiverClass).apply {
        putExtra(PalmNotificationManager.EXTRA_NOTIFICATION_ID, notificationId)
        putExtra(PalmNotificationManager.EXTRA_TITLE, title)
        putExtra(PalmNotificationManager.EXTRA_MESSAGE, message)
        putExtra(PalmNotificationManager.EXTRA_CHANNEL_ID, channelId)
    }

    private fun setExactAlarm(context: Context, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // setExactAndAllowWhileIdle works even in Doze mode
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            // SCHEDULE_EXACT_ALARM not granted (Android 14+ strict)
            // Fall back to inexact — will fire within a window near the target time
            Log.w(TAG, "Exact alarm permission denied, falling back to inexact: ${e.message}")
            am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }
}
