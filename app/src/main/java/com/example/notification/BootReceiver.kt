package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * PALM Boot Receiver
 *
 * AlarmManager alarms do NOT survive device reboots. This receiver is
 * registered for [Intent.ACTION_BOOT_COMPLETED] and [ACTION_MY_PACKAGE_REPLACED]
 * and re-schedules the notification worker immediately so alarms are restored
 * after a reboot or app update.
 */
class BootReceiver : BroadcastReceiver() {

    private val tag = "PalmBootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return

        Log.i(tag, "Boot/update detected — rescheduling PALM notification alarms")

        // Enqueue the notification worker immediately so it recomputes and
        // re-schedules all pending alarms right away.
        val immediateWork = OneTimeWorkRequestBuilder<PalmNotificationWorker>().build()
        WorkManager.getInstance(context).enqueue(immediateWork)
    }
}
