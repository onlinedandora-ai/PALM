package com.example.notification

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.data.database.PalmDatabase
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * PALM Notification Worker
 *
 * A [CoroutineWorker] that runs once at 08:00 AM daily (scheduled by
 * [PalmNotificationWorker.enqueueDailyWork]) and for each pending renewal/alert
 * calls [PalmAlarmScheduler.scheduleNotification] with:
 *   - triggerAtMillis  = the exact wall-clock time when the notification should appear
 *   - deadlineMillis   = the exact wall-clock time when the notification should expire
 *
 * Notification ID scheme (stable, no collisions):
 *   - Vehicle insurance:   1000 + vehicleId * 10 + leadDayOffset (0=14d,1=7d,2=1d)
 *   - Subscriptions:       2000 + subscriptionId * 10 + leadDayOffset (0=7d,1=3d,2=1d)
 *   - Budget over limit:   3000 + budgetIndex
 */
class PalmNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val tag = "PalmNotifWorker"

    override suspend fun doWork(): Result {
        return try {
            val db  = PalmDatabase.getDatabase(applicationContext)
            val dao = db.palmDao()

            val targetUserId = inputData.getString(KEY_USER_ID) ?: "user_primary"
            Log.i(tag, "Scheduling user-scoped notifications for userId: $targetUserId")

            val vehicles      = dao.getVehiclesForUser(targetUserId).first()
            val subscriptions = dao.getSubscriptionsForUser(targetUserId).first()
            val budgets       = dao.getBudgetsForUser(targetUserId).first()

            scheduleVehicleAlarms(vehicles)
            scheduleSubscriptionAlarms(subscriptions)
            scheduleBudgetAlerts(budgets)

            Log.i(tag, "Notification scheduling complete for user: $targetUserId")
            Result.success()
        } catch (e: Exception) {
            Log.e(tag, "Worker failed: ${e.message}", e)
            Result.retry()
        }
    }


    // ─────────────────────── Vehicle insurance ───────────────────────────

    /**
     * Schedules notifications 14 days, 7 days, and 1 day before insurance
     * expiry. Each notification auto-dismisses at the expiry deadline.
     */
    private fun scheduleVehicleAlarms(vehicles: List<com.example.data.database.VehicleEntity>) {
        val leadDays = listOf(14, 7, 1)

        vehicles.forEach { vehicle ->
            if (vehicle.insuranceExpiryDays <= 0) return@forEach // already expired

            // Compute the deadline epoch: midnight today + insuranceExpiryDays
            val deadlineMillis = midnightTodayMillis() +
                    TimeUnit.DAYS.toMillis(vehicle.insuranceExpiryDays.toLong())

            leadDays.forEachIndexed { index, daysLead ->
                if (daysLead > vehicle.insuranceExpiryDays) return@forEachIndexed

                val notifId = 1000 + (vehicle.id * 10).toInt() + index
                // Trigger at 09:00 AM, N days before deadline
                val triggerMillis = deadlineMillis -
                        TimeUnit.DAYS.toMillis(daysLead.toLong()) +
                        nineAmOffsetMillis()

                val title   = "🚗 ${vehicle.name} Insurance Renewal"
                val message = when (daysLead) {
                    1  -> "Your car insurance expires TOMORROW. Renew now to stay covered."
                    7  -> "Car insurance expires in 7 days (₹${String.format("%.2f", vehicle.insuranceCost)}). Don't forget to renew."
                    else -> "Car insurance for ${vehicle.name} renews in $daysLead days."
                }

                PalmAlarmScheduler.scheduleNotification(
                    context        = applicationContext,
                    notificationId = notifId,
                    triggerAtMillis = triggerMillis,
                    deadlineMillis  = deadlineMillis,   // auto-dismiss at expiry
                    title          = title,
                    message        = message,
                    channelId      = PalmNotificationManager.CHANNEL_RENEWALS
                )
                Log.d(tag, "Scheduled vehicle notif id=$notifId for ${vehicle.name}, lead=${daysLead}d")
            }
        }
    }

    // ──────────────────────── Subscriptions ──────────────────────────────

    /**
     * Schedules notifications 7 days, 3 days, and 1 day before each active
     * subscription renewal. Notification auto-dismisses at the renewal date.
     */
    private fun scheduleSubscriptionAlarms(
        subscriptions: List<com.example.data.database.SubscriptionEntity>
    ) {
        val leadDays = listOf(7, 3, 1)

        subscriptions.filter { !it.isCancelled }.forEach { sub ->
            if (sub.daysLeft <= 0) return@forEach

            val deadlineMillis = midnightTodayMillis() +
                    TimeUnit.DAYS.toMillis(sub.daysLeft.toLong())

            leadDays.forEachIndexed { index, daysLead ->
                if (daysLead > sub.daysLeft) return@forEachIndexed

                val notifId = 2000 + (sub.id * 10).toInt() + index
                val triggerMillis = deadlineMillis -
                        TimeUnit.DAYS.toMillis(daysLead.toLong()) +
                        nineAmOffsetMillis()

                val title   = "📺 ${sub.name} Renews Soon"
                val message = when (daysLead) {
                    1    -> "${sub.name} auto-renews TOMORROW for ₹${String.format("%.2f", sub.cost)}."
                    3    -> "${sub.name} auto-renews in 3 days (₹${String.format("%.2f", sub.cost)}/${sub.cycle.lowercase()})."
                    else -> "${sub.name} auto-renews in $daysLead days."
                }


                PalmAlarmScheduler.scheduleNotification(
                    context         = applicationContext,
                    notificationId  = notifId,
                    triggerAtMillis = triggerMillis,
                    deadlineMillis  = deadlineMillis,
                    title           = title,
                    message         = message,
                    channelId       = PalmNotificationManager.CHANNEL_RENEWALS
                )
                Log.d(tag, "Scheduled sub notif id=$notifId for ${sub.name}, lead=${daysLead}d")
            }
        }
    }

    // ───────────────────────── Budget alerts ─────────────────────────────

    /**
     * For each budget category at ≥90% utilisation, posts an immediate
     * notification that auto-dismisses at end of the current calendar month.
     */
    private fun scheduleBudgetAlerts(budgets: List<com.example.data.database.BudgetEntity>) {
        val endOfMonthMillis = endOfCurrentMonthMillis()

        budgets.forEachIndexed { index, budget ->
            val pct = if (budget.limitAmount > 0)
                (budget.spentAmount / budget.limitAmount) else 0.0

            when {
                pct >= 1.0 -> {
                    val notifId = 3000 + index
                    val title   = "⚠️ ${budget.category} Budget Exceeded"
                    val message = "You've spent \$${String.format("%.2f", budget.spentAmount)} " +
                            "— over your \$${String.format("%.2f", budget.limitAmount)} limit."
                    // Post immediately (triggerAt = now+5s so alarm fires right away)
                    PalmAlarmScheduler.scheduleNotification(
                        context         = applicationContext,
                        notificationId  = notifId,
                        triggerAtMillis = System.currentTimeMillis() + 5_000L,
                        deadlineMillis  = endOfMonthMillis,
                        title           = title,
                        message         = message,
                        channelId       = PalmNotificationManager.CHANNEL_BUDGET
                    )
                }
                pct >= 0.90 -> {
                    val notifId = 3100 + index
                    val pctInt  = (pct * 100).toInt()
                    val title   = "💰 ${budget.category} Budget Warning"
                    val message = "You've used $pctInt% of your \$${String.format("%.2f", budget.limitAmount)} budget this month."
                    PalmAlarmScheduler.scheduleNotification(
                        context         = applicationContext,
                        notificationId  = notifId,
                        triggerAtMillis = System.currentTimeMillis() + 5_000L,
                        deadlineMillis  = endOfMonthMillis,
                        title           = title,
                        message         = message,
                        channelId       = PalmNotificationManager.CHANNEL_BUDGET
                    )
                }
            }
        }
    }

    // ─────────────────────────── Time helpers ────────────────────────────

    /**
     * Midnight of the current day in the device's local timezone (epoch ms).
     */
    private fun midnightTodayMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * Offset in millis from midnight → 09:00 AM.
     * Notifications fire at 9 AM so they arrive in the user's morning.
     */
    private fun nineAmOffsetMillis(): Long =
        TimeUnit.HOURS.toMillis(9)

    /**
     * Epoch millis for the last millisecond of the current calendar month
     * (used as the budget-alert dismiss deadline).
     */
    private fun endOfCurrentMonthMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    // ────────────────────── Static scheduling helpers ─────────────────────

    companion object {

        const val KEY_USER_ID = "palm_user_id"
        private const val WORK_NAME_DAILY = "palm_daily_notification_scheduler"

        /**
         * Enqueue a daily periodic WorkManager job that runs at ~08:00 AM each
         * day and re-schedules all notification alarms for the specific user.
         */
        fun enqueueDailyWork(context: Context, userId: String = "user_primary") {
            val initialDelayMs = computeInitialDelayToEightAm()
            val inputData = Data.Builder().putString(KEY_USER_ID, userId).build()

            val dailyRequest = PeriodicWorkRequestBuilder<PalmNotificationWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setInputData(inputData)
                .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_DAILY,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyRequest
            )
            Log.i("PalmNotifWorker", "Daily work enqueued for user $userId, initial delay=${initialDelayMs}ms")
        }

        /**
         * Run the worker once immediately for the specific user.
         */
        fun enqueueImmediate(context: Context, userId: String = "user_primary") {
            val inputData = Data.Builder().putString(KEY_USER_ID, userId).build()
            val request = OneTimeWorkRequestBuilder<PalmNotificationWorker>()
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }


        /**
         * Computes milliseconds from now until 08:00 AM tomorrow (or today if
         * it's currently before 08:00 AM).
         */
        private fun computeInitialDelayToEightAm(): Long {
            val now    = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            // If 08:00 AM has already passed today, target tomorrow
            if (now.after(target)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            return (target.timeInMillis - now.timeInMillis).coerceAtLeast(0L)
        }
    }
}
