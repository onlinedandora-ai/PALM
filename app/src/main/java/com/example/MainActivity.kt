package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notification.PalmNotificationManager
import com.example.notification.PalmNotificationWorker
import com.example.ui.PalmMainApp
import com.example.ui.PalmViewModel
import com.example.ui.theme.PalmTheme

class MainActivity : ComponentActivity() {

    // ── POST_NOTIFICATIONS permission launcher (Android 13+) ──────────────
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                // Permission granted — schedule the daily notification worker
                PalmNotificationWorker.enqueueDailyWork(this)
            }
            // If denied, the app works normally but no notifications are sent
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 0. Initialize Firebase Auth & Security Manager
        com.example.auth.FirebaseAuthManager.init(this)

        // 1. Create notification channels (idempotent — safe to call every launch)
        PalmNotificationManager.createChannels(this)


        // 2. Request POST_NOTIFICATIONS on Android 13+; on lower APIs just enqueue
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // Already granted — ensure the daily worker is running
                        PalmNotificationWorker.enqueueDailyWork(this)
                    }
                    else -> {
                        // Request permission — worker is started in the callback above
                        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            else -> {
                // Android 12 and below — no runtime permission needed
                PalmNotificationWorker.enqueueDailyWork(this)
            }
        }

        setContent {
            PalmTheme {
                val viewModel: PalmViewModel = viewModel()
                PalmMainApp(viewModel = viewModel)
            }
        }
    }
}
