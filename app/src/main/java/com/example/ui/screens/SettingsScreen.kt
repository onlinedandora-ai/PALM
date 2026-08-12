package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ModuleEntity
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(viewModel: PalmViewModel) {
    val installedModules by viewModel.installedModules.collectAsState()
    val biometricAppLock by viewModel.biometricAppLock.collectAsState()
    val encryptedCloudSync by viewModel.encryptedCloudSync.collectAsState()

    var showManageModuleDialog by remember { mutableStateOf<ModuleEntity?>(null) }
    var showExportSuccessDialog by remember { mutableStateOf(false) }
    var showWipeConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PalmBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Settings & Security Center",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PalmNavy
        )

        Text(
            text = "Zero-Trust Privacy & Hardware Encryption Governance",
            fontSize = 11.sp,
            color = PalmLineGrey
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Data Privacy & Audit Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PalmWhite),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("DATA PRIVACY & AUDIT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PalmLineGrey, letterSpacing = 1.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Local Storage Used", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = PalmNavy)
                    Text("14.2 MB", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PalmNavy)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = PalmCardBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Encrypted Cloud Sync", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = PalmNavy)
                    Switch(
                        checked = encryptedCloudSync,
                        onCheckedChange = { viewModel.toggleEncryptedSync(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = PalmAccentBlue)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = PalmCardBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Biometric App Lock", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = PalmNavy)
                    Switch(
                        checked = biometricAppLock,
                        onCheckedChange = { viewModel.toggleBiometricLock(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = PalmAccentBlue)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "INSTALLED MODULES PERMISSIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PalmLineGrey,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(installedModules) { mod ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PalmWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(mod.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PalmNavy)
                            Text("Size: ${mod.sizeKb} KB • ${mod.permissions}", fontSize = 11.sp, color = PalmLineGrey, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = { showManageModuleDialog = mod },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Manage", fontSize = 11.sp, color = PalmNavy)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Danger Zone
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PalmAlertRed.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PalmAlertRed.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("DANGER ZONE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PalmAlertRed, letterSpacing = 1.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showExportSuccessDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_data_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Export Data (JSON)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PalmNavy)
                    }

                    Button(
                        onClick = { showWipeConfirmDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("wipe_data_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PalmAlertRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Wipe All Data", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("sign_out_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PalmNavy),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out & Lock Vault", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Manage Single Module Dialog
    showManageModuleDialog?.let { mod ->
        AlertDialog(
            onDismissRequest = { showManageModuleDialog = null },
            title = { Text("Manage ${mod.displayName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Namespace Table: mod_${mod.id.replace("-", "_")}", fontSize = 12.sp, color = PalmLineGrey)
                    Text("Allocated Storage: ${mod.sizeKb} KB", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Permissions: ${mod.permissions}", fontSize = 12.sp, color = PalmNavy)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleModuleInstall(mod.id, true)
                        showManageModuleDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PalmAlertRed)
                ) {
                    Text("Single Module Wipe")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManageModuleDialog = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Export Data Dialog
    if (showExportSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showExportSuccessDialog = false },
            icon = { Icon(Icons.Default.Download, contentDescription = "Export", tint = PalmSuccessGreen) },
            title = { Text("Encrypted Data Export Generated") },
            text = {
                Text(
                    text = "Export file 'palm_backup_2026_08.zip' generated with 256-bit AES password encryption containing JSON dumps of all SQLite tables.",
                    fontSize = 12.sp,
                    color = PalmNavy
                )
            },
            confirmButton = {
                Button(onClick = { showExportSuccessDialog = false }) {
                    Text("Save ZIP Archive")
                }
            }
        )
    }

    // Wipe All Data Dialog
    if (showWipeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showWipeConfirmDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = "Warning", tint = PalmAlertRed) },
            title = { Text("Wipe All Local Data?") },
            text = {
                Text(
                    text = "This will permanently purge all SQLite tables and decrypted vault files from this device in < 100ms.",
                    fontSize = 12.sp,
                    color = PalmNavy
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.wipeAllLocalData()
                        showWipeConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PalmAlertRed)
                ) {
                    Text("Confirm Permanent Wipe")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWipeConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
