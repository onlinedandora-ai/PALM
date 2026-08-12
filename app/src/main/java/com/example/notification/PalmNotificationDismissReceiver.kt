package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * PALM Notification Dismiss Receiver
 *
 * Receives the **deadline alarm** and cancels (auto-dismisses) any active
 * notification for that event. This implements the "correct end time" behaviour:
 * once an event's deadline has passed, its notification disappears automatically
 * even if the user never tapped it.
 *
 * Scheduled by [PalmAlarmScheduler.scheduleNotification] as a second alarm
 * set to fire at [deadlineMillis].
 */
class PalmNotificationDismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(
            PalmNotificationManager.EXTRA_NOTIFICATION_ID,
            -1
        )
        if (notificationId == -1) return

        // Cancel the notification from the tray
        PalmNotificationManager.cancelNotification(context, notificationId)
    }
}
