package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * PALM Notification Receiver
 *
 * Receives the trigger alarm broadcast and posts the notification to the
 * system notification tray. All payload (title, message, channel, id) is
 * carried in the Intent extras set by [PalmAlarmScheduler].
 */
class PalmNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(PalmNotificationManager.EXTRA_NOTIFICATION_ID, -1)
        val title          = intent.getStringExtra(PalmNotificationManager.EXTRA_TITLE) ?: return
        val message        = intent.getStringExtra(PalmNotificationManager.EXTRA_MESSAGE) ?: return
        val channelId      = intent.getStringExtra(PalmNotificationManager.EXTRA_CHANNEL_ID)
            ?: PalmNotificationManager.CHANNEL_RENEWALS

        if (notificationId == -1) return

        PalmNotificationManager.postNotification(
            context      = context,
            notificationId = notificationId,
            channelId    = channelId,
            title        = title,
            message      = message
        )
    }
}
