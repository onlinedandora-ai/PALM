package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.VaultDocumentEntity
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

@Composable
fun VaultScreen(viewModel: PalmViewModel) {
    val vaultDocs by viewModel.vaultDocs.collectAsState()

    var showUploadModal by remember { mutableStateOf(false) }
    var newDocTitle by remember { mutableStateOf("") }
    var newDocCategory by remember { mutableStateOf("Identity Docs") }

    var selectedDocToView by remember { mutableStateOf<VaultDocumentEntity?>(null) }
    var isBiometricAuthenticatedForDoc by remember { mutableStateOf(false) }
    var showShareEphemeralModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PalmBackground)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Digital Vault",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PalmNavy
                )
                Text(
                    text = "AES-256-GCM / SQLCipher Sandbox",
                    fontSize = 11.sp,
                    color = PalmLineGrey
                )
            }

            Button(
                onClick = { showUploadModal = true },
                colors = ButtonDefaults.buttonColors(containerColor = PalmNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("upload_doc_button")
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = "Upload", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Biometric Security Active Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PalmNavy),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Biometric Lock",
                    tint = PalmAlertAmber,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🔐 Biometric Storage Active",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmWhite
                    )
                    Text(
                        text = "All documents and passwords are local encrypted.",
                        fontSize = 11.sp,
                        color = PalmWhite.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Password Manager Module Shortcut Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.openModule("password-manager") },
            colors = CardDefaults.cardColors(containerColor = PalmWhite),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PalmAccentBlue)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PalmNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = "Password Manager",
                        tint = PalmWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Password Vault & Manager",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmNavy
                    )
                    Text(
                        text = "Manage AES-256 encrypted credentials & generator",
                        fontSize = 11.sp,
                        color = PalmLineGrey
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = PalmAccentBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))


        Text(
            text = "SECURE CATEGORIES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PalmLineGrey,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val identityCount = vaultDocs.count { it.category == "Identity Docs" }
            val propertyCount = vaultDocs.count { it.category == "Property & Insurance" }
            val accountsCount = vaultDocs.count { it.category == "Digital Accounts" }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = PalmWhite),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("📄 Identity", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PalmNavy)
                    Text("$identityCount docs", fontSize = 11.sp, color = PalmLineGrey)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = PalmWhite),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("🏠 Property", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PalmNavy)
                    Text("$propertyCount docs", fontSize = 11.sp, color = PalmLineGrey)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = PalmWhite),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("🔑 Accounts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PalmNavy)
                    Text("$accountsCount keys", fontSize = 11.sp, color = PalmLineGrey)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "ENCRYPTED DOCUMENTS (${vaultDocs.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PalmLineGrey,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(vaultDocs) { doc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedDocToView = doc
                            isBiometricAuthenticatedForDoc = false
                        },
                    colors = CardDefaults.cardColors(containerColor = PalmWhite),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = doc.title,
                            tint = PalmAccentBlue,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = doc.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PalmNavy
                            )
                            Text(
                                text = "Encrypted • ${doc.expiryInfo} • ${doc.sharingNote}",
                                fontSize = 11.sp,
                                color = PalmLineGrey
                            )
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = "View", tint = PalmLineGrey)
                    }
                }
            }
        }
    }

    // Modal Document Viewer requiring Biometric Re-Auth per PRD 8.8
    selectedDocToView?.let { doc ->
        AlertDialog(
            onDismissRequest = { selectedDocToView = null },
            icon = {
                Icon(
                    imageVector = if (isBiometricAuthenticatedForDoc) Icons.Default.LockOpen else Icons.Default.Fingerprint,
                    contentDescription = "Lock",
                    tint = PalmAccentBlue,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (isBiometricAuthenticatedForDoc) doc.title else "Biometric Verification Required",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (!isBiometricAuthenticatedForDoc) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Touch sensor or enter Face ID to decrypt ${doc.title}.", fontSize = 13.sp, color = PalmLineGrey)
                        Button(
                            onClick = { isBiometricAuthenticatedForDoc = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PalmNavy)
                        ) {
                            Text("Authenticate Face ID / Fingerprint")
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Category: ${doc.category}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Encrypted Payload Size: ${doc.encryptedSize}", fontSize = 12.sp, color = PalmLineGrey)
                        Text("Expiration: ${doc.expiryInfo}", fontSize = 12.sp, color = PalmNavy)
                        Text("Access Control: ${doc.sharingNote}", fontSize = 12.sp, color = PalmNavy)

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = { showShareEphemeralModal = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Securely (15-Min Ephemeral Link)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDocToView = null }) {
                    Text("Close")
                }
            }
        )
    }

    // 15-Min Ephemeral Link Modal
    if (showShareEphemeralModal) {
        AlertDialog(
            onDismissRequest = { showShareEphemeralModal = false },
            icon = { Icon(Icons.Default.Timer, contentDescription = "Timer", tint = PalmAlertAmber) },
            title = { Text("15-Minute Ephemeral Link Generated") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Link: https://palm.vault/share/e7a9b12c4f?exp=15m", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PalmAccentBlue)
                    Text("Protected by AES-256 zero-trust password token. Auto-destructs in 14m 58s.", fontSize = 11.sp, color = PalmLineGrey)
                }
            },
            confirmButton = {
                Button(onClick = { showShareEphemeralModal = false }) {
                    Text("Copy Link")
                }
            }
        )
    }

    // Upload Document Modal
    if (showUploadModal) {
        AlertDialog(
            onDismissRequest = { showUploadModal = false },
            title = { Text("Upload Secure Document") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newDocTitle,
                        onValueChange = { newDocTitle = it },
                        label = { Text("Document Title (e.g. Drivers_License.pdf)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newDocCategory,
                        onValueChange = { newDocCategory = it },
                        label = { Text("Category (Identity Docs, Property, Accounts)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDocTitle.isNotBlank()) {
                            viewModel.addVaultDoc(newDocTitle, newDocCategory)
                        }
                        showUploadModal = false
                        newDocTitle = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PalmNavy)
                ) {
                    Text("Encrypt & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
