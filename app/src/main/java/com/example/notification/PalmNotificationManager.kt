package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

/**
 * PALM Notification Manager
 *
 * Manages notification channels and provides helpers to post and cancel
 * notifications. Channels are created once on app start; posting is safe
 * to call from any BroadcastReceiver or Worker.
 */
object PalmNotificationManager {

    // ──────────────────────────── Channel IDs ────────────────────────────

    const val CHANNEL_RENEWALS = "palm_renewals"
    const val CHANNEL_BUDGET   = "palm_budget"
    const val CHANNEL_REMINDERS = "palm_reminders"
    const val CHANNEL_AUTH      = "palm_auth"

    // ────────────────────────── Intent extras ────────────────────────────

    const val EXTRA_NOTIFICATION_ID   = "palm_notif_id"
    const val EXTRA_TITLE             = "palm_title"
    const val EXTRA_MESSAGE           = "palm_message"
    const val EXTRA_CHANNEL_ID        = "palm_channel_id"

    // ─────────────────────── Create channels once ────────────────────────

    /**
     * Call this from [MainActivity.onCreate]. Safe to call repeatedly —
     * Android no-ops if the channel already exists.
     */
    fun createChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_AUTH,
                "Authentication & Security",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Security verification OTP notifications"
                enableVibration(true)
            }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_RENEWALS,
                "Renewals & Deadlines",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Vehicle insurance, subscription, and document renewal alerts"
                enableVibration(true)
            }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_BUDGET,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Household budget threshold and over-limit alerts"
            }
        )

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_REMINDERS,
                "PALM Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General PALM lifestyle reminders"
            }
        )
    }


    // ─────────────────────────── Post a notification ──────────────────────

    /**
     * Posts a notification. The tap action deep-links back to the app.
     *
     * @param notificationId Stable ID — use the same ID for a given event so
     *                       it replaces any existing notification for that event.
     */
    fun postNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        message: String
    ) {
        // Tapping the notification opens the app
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val tapPending = PendingIntent.getActivity(
            context,
            notificationId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(tapPending)
            .setAutoCancel(true)                     // dismiss on tap
            .setPriority(
                if (channelId == CHANNEL_RENEWALS)
                    NotificationCompat.PRIORITY_HIGH
                else
                    NotificationCompat.PRIORITY_DEFAULT
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission not granted — silently ignore
        }
    }

    // ──────────────────────── Cancel a notification ───────────────────────

    /**
     * Cancels (dismisses) a notification by its ID. Called by
     * [PalmNotificationDismissReceiver] when the deadline alarm fires.
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}
