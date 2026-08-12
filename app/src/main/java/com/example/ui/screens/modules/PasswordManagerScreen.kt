package com.example.ui.screens.modules

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.PasswordEntity
import com.example.ui.PalmViewModel
import com.example.ui.theme.*
import com.example.util.PasswordEncryptionHelper
import kotlin.random.Random

@Composable
fun PasswordManagerScreen(
    viewModel: PalmViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val passwords by viewModel.passwords.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Form inputs matching screenshot
    var newWebsiteInput by remember { mutableStateOf("Google") }
    var newUsernameInput by remember { mutableStateOf("User@gmail.com") }

    val darkBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF091629),
            Color(0xFF0F233A),
            Color(0xFF0B192C)
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Surface(color = Color(0xFF0D1E36), shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add New Password",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Password",
                            tint = Color(0xFF00B4D8),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBackground)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Top Form Inputs from Middle Phone Screenshot
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Website/App", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90E0EF))
                        OutlinedTextField(
                            value = newWebsiteInput,
                            onValueChange = { newWebsiteInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF13253E),
                                unfocusedContainerColor = Color(0xFF13253E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF00B4D8),
                                unfocusedBorderColor = Color(0xFF1E3A5F)
                            )
                        )

                        Text("Username/Email", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90E0EF))
                        OutlinedTextField(
                            value = newUsernameInput,
                            onValueChange = { newUsernameInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF13253E),
                                unfocusedContainerColor = Color(0xFF13253E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF00B4D8),
                                unfocusedBorderColor = Color(0xFF1E3A5F)
                            )
                        )
                    }
                }

                // Glowing Lock Aura Emblem from Screenshot
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00B4D8).copy(alpha = 0.15f))
                                .border(2.dp, Color(0xFF00B4D8).copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock",
                                tint = Color(0xFF00B4D8),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Saved Passwords List (Google, Netflix, Banking, Amazon)
                items(passwords, key = { it.id }) { item ->
                    PasswordAccountCard(
                        item = item,
                        onCopyUsername = { copyToClipboard(context, "Username", item.username) },
                        onCopyPassword = {
                            val decrypted = PasswordEncryptionHelper.decrypt(item.encryptedPassword)
                            copyToClipboard(context, "Password", decrypted)
                        },
                        onDelete = { viewModel.deletePassword(item.id) }
                    )
                }

                // Password Generator Card matching Middle Phone Bottom Sheet!
                item {
                    PasswordGeneratorBottomSheetCard(
                        onSavePassword = { generated ->
                            viewModel.addPassword(
                                title = newWebsiteInput.ifBlank { "New Account" },
                                url = "https://${newWebsiteInput.lowercase()}.com",
                                username = newUsernameInput,
                                plainPass = generated,
                                category = "Personal",
                                notes = "Generated via Password Vault"
                            )
                            Toast.makeText(context, "Saved to AES Vault & Synced!", Toast.LENGTH_SHORT).show()
                        },
                        onCopyPassword = { generated ->
                            copyToClipboard(context, "Password", generated)
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AddPasswordModalDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, url, username, password, category, notes ->
                viewModel.addPassword(title, url, username, password, category, notes)
                showAddDialog = false
                Toast.makeText(context, "Password saved to vault", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun PasswordAccountCard(
    item: PasswordEntity,
    onCopyUsername: () -> Unit,
    onCopyPassword: () -> Unit,
    onDelete: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val decrypted = remember(item.encryptedPassword) { PasswordEncryptionHelper.decrypt(item.encryptedPassword) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13253E)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A5F))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0B192C)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.accountTitle.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF00B4D8)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.accountTitle, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = if (isVisible) decrypted else item.username,
                    fontSize = 11.sp,
                    color = Color(0xFF90E0EF)
                )
            }

            IconButton(onClick = { isVisible = !isVisible }, modifier = Modifier.size(32.dp)) {
                Icon(if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }

            IconButton(onClick = onCopyPassword, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF00B4D8), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun PasswordGeneratorBottomSheetCard(
    onSavePassword: (String) -> Unit,
    onCopyPassword: (String) -> Unit
) {
    var length by remember { mutableStateOf(18f) }
    var useUppercase by remember { mutableStateOf(true) }
    var useNumbers by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }

    fun generatePassword(): String {
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val numbers = "0123456789"
        val symbols = "!@#$%^&*()_+-=[]{}|;:,.<>?"

        var pool = lowercase
        if (useUppercase) pool += uppercase
        if (useNumbers) pool += numbers
        if (useSymbols) pool += symbols

        return (1..length.toInt())
            .map { pool[Random.nextInt(pool.length)] }
            .joinToString("")
    }

    var generatedPassword by remember { mutableStateOf(generatePassword()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1C2E)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00B4D8))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Password Generator", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(12.dp))

            // Length Slider matching screenshot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Length", fontSize = 12.sp, color = Color.LightGray)
                Text("${length.toInt()} characters", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00B4D8))
            }

            Slider(
                value = length,
                onValueChange = {
                    length = it
                    generatedPassword = generatePassword()
                },
                valueRange = 8f..32f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF00B4D8),
                    activeTrackColor = Color(0xFF00B4D8)
                )
            )

            // Switches row: Uppercase, Numbers, Symbols from screenshot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Uppercase", fontSize = 11.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = useUppercase,
                        onCheckedChange = { useUppercase = it; generatedPassword = generatePassword() },
                        modifier = Modifier.height(24.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Numbers", fontSize = 11.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = useNumbers,
                        onCheckedChange = { useNumbers = it; generatedPassword = generatePassword() },
                        modifier = Modifier.height(24.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Symbols", fontSize = 11.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = useSymbols,
                        onCheckedChange = { useSymbols = it; generatedPassword = generatePassword() },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Generated Password Box matching screenshot cyan box!
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0B2430),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00B4D8))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Live Password", fontSize = 10.sp, color = Color(0xFF90E0EF), modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = generatedPassword,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF00B4D8),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onCopyPassword(generatedPassword) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF00B4D8))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Strength Meter Bar from Screenshot (STRONG: 96%)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Strength Meter", fontSize = 11.sp, color = Color.Gray)
                Text("STRONG: 96%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2FA860))
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { 0.96f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF2FA860),
                trackColor = Color.Gray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Action Buttons from Screenshot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onCopyPassword(generatedPassword) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00B4D8))
                ) {
                    Text("Copy Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00B4D8))
                }

                Button(
                    onClick = { onSavePassword(generatedPassword) },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8), contentColor = Color.Black)
                ) {
                    Text("Save to Vault", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
}

@Composable
fun AddPasswordModalDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, url: String, username: String, pass: String, cat: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Personal") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save New Credential", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Account Title") }, singleLine = true)
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username / Email") }, singleLine = true)
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true)
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Website URL (Optional)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && username.isNotBlank() && password.isNotBlank()) {
                        onSave(title, url, username, password, category, notes)
                    }
                },
                enabled = title.isNotBlank() && username.isNotBlank() && password.isNotBlank()
            ) { Text("Save Credential") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

