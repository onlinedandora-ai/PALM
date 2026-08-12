package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

// ─── Module icon & accent colour ─────────────────────────────────────────────
private fun moduleIconInfo(id: String): Pair<ImageVector, Color> = when (id) {
    "daily-sync"        -> Pair(Icons.Default.CalendarToday,  Color(0xFF2F6FED))
    "household-finance" -> Pair(Icons.Default.AccountBalance,  Color(0xFF2FA860))
    "vehicle-hub"       -> Pair(Icons.Default.DirectionsCar,   Color(0xFF2F6FED))
    "subscriptions"     -> Pair(Icons.Default.Layers,          Color(0xFF9C27B0))
    "digital-vault"     -> Pair(Icons.Default.Lock,            Color(0xFFF4B73F))
    "password-manager"  -> Pair(Icons.Default.VpnKey,          Color(0xFFE53935))
    "healthcare"        -> Pair(Icons.Default.Extension,       Color(0xFF00BCD4))
    else                -> Pair(Icons.Default.Extension,       Color(0xFF9AA1AB))
}

// ─── Palette ─────────────────────────────────────────────────────────────────
private val PageBg       = Color(0xFFF4F6FB)
private val CardWhite    = Color(0xFFFFFFFF)
private val NavyText     = Color(0xFF1A2340)
private val GreenTag     = Color(0xFF2A9D5C)
private val DividerColor = Color(0xFFE8ECF2)
private val AmberBorder  = Color(0xFFFFB703)
private val AmberBg      = Color(0xFFFFFBF0)
private val UrgentOrange = Color(0xFFF4A118)
private val AccentBlue   = Color(0xFF2F6FED)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(viewModel: PalmViewModel) {
    val userName             by viewModel.userName.collectAsState()
    val userPhone            by viewModel.userPhone.collectAsState()
    val isDiscoveryDismissed by viewModel.isDiscoveryDismissed.collectAsState()
    val insights             by viewModel.insights.collectAsState()
    val installedModules     by viewModel.installedModules.collectAsState()

    var showNotificationsModal by remember { mutableStateOf(false) }
    var showProfileModal       by remember { mutableStateOf(false) }

    val moduleRows = installedModules.chunked(2)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        // ── HEADER ──────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good morning, $userName",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(GreenTag)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "PALM Local Engine • AES-256 Active",
                            fontSize = 12.sp,
                            color = GreenTag,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notification bell with badge
                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(
                            onClick = { showNotificationsModal = true },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(CardWhite)
                                .testTag("notification_button")
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = NavyText,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        // Badge
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                                .offset(x = 2.dp, y = (-2).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("2", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AccentBlue)
                            .clickable { showProfileModal = true }
                            .testTag("profile_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "S",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── DISCOVERY BANNER ─────────────────────────────────────────────────
        if (!isDiscoveryDismissed) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("discovery_banner"),
                    colors = CardDefaults.cardColors(containerColor = AmberBg),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberBorder)
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
                                .clip(CircleShape)
                                .background(NavyText.copy(alpha = 0.07f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Discovery",
                                tint = NavyText,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DISCOVERY: ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NavyText,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Try 'Property Taxes' Module",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyText
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Automate escrow calculations & deadlines.",
                                fontSize = 12.sp,
                                color = Color(0xFF4A5568)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                viewModel.toggleModuleInstall("property-taxes", false)
                                viewModel.dismissDiscoveryBanner()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyText),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Add", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = { viewModel.dismissDiscoveryBanner() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = Color(0xFF4A5568),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))
            }
        }

        // ── TODAY INSIGHTS ────────────────────────────────────────────────────
        item {
            SectionLabel(text = "TODAY INSIGHTS")
            Spacer(modifier = Modifier.height(12.dp))

            if (insights.isEmpty()) {
                Text(
                    "No active alerts.",
                    fontSize = 13.sp,
                    color = Color(0xFF8C9BAB)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(insights) { insight ->
                        val badgeColor = try {
                            Color(android.graphics.Color.parseColor(insight.badgeColorHex))
                        } catch (e: Exception) { UrgentOrange }

                        Card(
                            modifier = Modifier
                                .width(270.dp)
                                .clickable { viewModel.openModule(insight.route) }
                                .shadow(2.dp, RoundedCornerShape(18.dp)),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = insight.moduleName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8C9BAB),
                                        letterSpacing = 0.5.sp
                                    )
                                    Surface(
                                        color = badgeColor.copy(alpha = 0.13f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = insight.badgeText,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = insight.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyText,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = insight.description,
                                    fontSize = 12.sp,
                                    color = Color(0xFF4A5568),
                                    maxLines = 2,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "${insight.actionText} →",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
        }

        // ── YOUR MODULES header ───────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel(text = "YOUR MODULES")
                TextButton(onClick = { viewModel.selectTab(1) }) {
                    Text(
                        "Edit / Add More",
                        fontSize = 12.sp,
                        color = AccentBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // ── MODULE GRID (2-col) ───────────────────────────────────────────────
        items(moduleRows) { rowMods ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowMods.forEach { mod ->
                    val (icon, iconBg) = moduleIconInfo(mod.id)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.openModule(mod.id) }
                            .shadow(1.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(iconBg.copy(alpha = 0.13f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = mod.displayName,
                                    tint = iconBg,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = mod.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NavyText,
                                maxLines = 2,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Spacer for odd module count
                if (rowMods.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }

    // ── Notification Modal ────────────────────────────────────────────────────
    if (showNotificationsModal) {
        AlertDialog(
            onDismissRequest = { showNotificationsModal = false },
            icon = {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = AccentBlue
                )
            },
            title = { Text("Local System Alerts", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HomeNotifItem("Vehicle Hub: Insurance renewal due in 5 days (₹640.00).", UrgentOrange)
                    HomeNotifItem("Household Finance: Utilities budget limit reached (₹450 / ₹450).", Color(0xFFE53935))
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsModal = false }) {
                    Text("Dismiss All", color = AccentBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // ── Profile Modal ──────────────────────────────────────────────────────────
    if (showProfileModal) {
        AlertDialog(
            onDismissRequest = { showProfileModal = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(AccentBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercaseChar()?.toString() ?: "S",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            },
            title = { Text("Household Operator Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HomeProfileRow("Name", userName)
                    HomeProfileRow("Primary Phone", userPhone.ifEmpty { "Not registered" })
                    HomeProfileRow("Household ID", "PALM-HH-88219")
                    HomeProfileRow("Local DB", "SQLCipher 256-bit AES")
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileModal = false }) {
                    Text("Close", color = AccentBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ─── Section Label ───────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF8C9BAB),
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun HomeNotifItem(text: String, dotColor: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 13.sp, color = Color(0xFF2D3748))
    }
}

@Composable
private fun HomeProfileRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("$label: ", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NavyText)
        Text(value, fontSize = 13.sp, color = Color(0xFF4A5568))
    }
}
