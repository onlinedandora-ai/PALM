package com.example.ui.screens.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.EventEntity
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

@Composable
fun DailySyncScreen(viewModel: PalmViewModel, onBack: () -> Unit) {
    val events by viewModel.events.collectAsState()
    val smsLogs by viewModel.smsLogs.collectAsState()

    var reassignEventTarget by remember { mutableStateOf<EventEntity?>(null) }
    var newAssigneeName by remember { mutableStateOf("Alex (Dad)") }
    var newAssigneePhone by remember { mutableStateOf("") }
    var showSmsLogDialog by remember { mutableStateOf(false) }

    var selectedDayIndex by remember { mutableStateOf(1) } // Default Tuesday 15th

    val darkBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF091629),
            Color(0xFF0F233A),
            Color(0xFF0B192C)
        )
    )

    val weekDays = listOf(
        Pair("Mon", "14"),
        Pair("Tu", "15"),
        Pair("We", "16"),
        Pair("Th", "17"),
        Pair("Fr", "18"),
        Pair("Sa", "19"),
        Pair("Su", "20")
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Daily & Family Sync",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { showSmsLogDialog = true }) {
                        BadgedBox(badge = { if (smsLogs.isNotEmpty()) Badge { Text("${smsLogs.size}") } }) {
                            Icon(Icons.Default.Sms, contentDescription = "SMS Logs", tint = Color(0xFF00B4D8))
                        }
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

                // Date Picker Header from Screenshot: < Today, Nov 15, 2026 >
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = Color.White, modifier = Modifier.size(24.dp))
                        Text(
                            text = "Today, Nov 15, 2026",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                // Week Days Strip matching screenshot (Mon 14, Tu 15 [Selected], We 16, Th 17...)
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        items(weekDays.size) { index ->
                            val item = weekDays[index]
                            val isSelected = index == selectedDayIndex

                            Box(
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF00B4D8) else Color(0xFF13253E))
                                    .clickable { selectedDayIndex = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(item.first, fontSize = 11.sp, color = if (isSelected) Color.Black else Color.Gray)
                                    Text(item.second, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.Black else Color.White)
                                }
                            }
                        }
                    }
                }

                // Schedule Timeline Items from Screenshot:
                // 09:00 AM - Project Meeting (Work) [Green Pill]
                item {
                    TimelineItemRow(
                        time = "09:00 AM",
                        title = "Project Meeting",
                        subtitle = "(Work)",
                        badgeText = null,
                        badgeIcon = Icons.Default.Notifications,
                        containerColor = Color(0xFF1B4D3E),
                        accentColor = Color(0xFF2FA860)
                    )
                }

                // 01:00 PM - School Run (Sarah) [Blue Pill with Badge 3]
                item {
                    TimelineItemRow(
                        time = "01:00 PM",
                        title = "School Run",
                        subtitle = "(Sarah)",
                        badgeText = "3",
                        badgeIcon = null,
                        containerColor = Color(0xFF133254),
                        accentColor = Color(0xFF00B4D8),
                        onReassignClick = {
                            reassignEventTarget = EventEntity(title = "School Run (Sarah)", dayOfWeek = "TUE 15", time = "01:00 PM", assignedTo = "Sarah", status = "Confirmed", isHandoff = true)
                        }
                    )
                }

                // 03:00 PM - Groceries (Whole Foods) [Blue Pill with Badge 1]
                item {
                    TimelineItemRow(
                        time = "03:00 PM",
                        title = "Groceries",
                        subtitle = "(Whole Foods)",
                        badgeText = "1",
                        badgeIcon = null,
                        containerColor = Color(0xFF133254),
                        accentColor = Color(0xFF00B4D8)
                    )
                }

                // 06:00 PM - Family Dinner (Restaurant) [Red Pill with Badge 1]
                item {
                    TimelineItemRow(
                        time = "06:00 PM",
                        title = "Family Dinner",
                        subtitle = "(Restaurant)",
                        badgeText = "!",
                        badgeIcon = null,
                        containerColor = Color(0xFF4A1B24),
                        accentColor = Color(0xFFE53935)
                    )
                }

                // Family Status Section from Screenshot!
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Family Status", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        FamilyMemberAvatar(name = "Sarah", role = "Mom", isOnline = true)
                        FamilyMemberAvatar(name = "Alex", role = "Dad", isOnline = true)
                        FamilyMemberAvatar(name = "Liam", role = "Son", isOnline = false)
                    }
                }

                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }

    // Reassign via SMS Modal
    reassignEventTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { reassignEventTarget = null },
            icon = { Icon(Icons.Default.Sms, contentDescription = null, tint = Color(0xFF00B4D8)) },
            title = { Text("Reassign Handoff via SMS") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Reassigning: ${target.title}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    OutlinedTextField(
                        value = newAssigneeName,
                        onValueChange = { newAssigneeName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAssigneePhone,
                        onValueChange = { newAssigneePhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reassignEvent(target.id, newAssigneeName, newAssigneePhone, target.title)
                        reassignEventTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D1E36))
                ) {
                    Text("Send SMS")
                }
            },
            dismissButton = {
                TextButton(onClick = { reassignEventTarget = null }) { Text("Cancel") }
            }
        )
    }

    if (showSmsLogDialog) {
        AlertDialog(
            onDismissRequest = { showSmsLogDialog = false },
            title = { Text("SMS Fallback Logs") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (smsLogs.isEmpty()) {
                        Text("No SMS sent yet.", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        smsLogs.forEach { log ->
                            Text("• To ${log.recipientName}: ${log.messageText}", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showSmsLogDialog = false }) { Text("Close") } }
        )
    }
}

@Composable
fun TimelineItemRow(
    time: String,
    title: String,
    subtitle: String,
    badgeText: String?,
    badgeIcon: androidx.compose.ui.graphics.vector.ImageVector?,
    containerColor: Color,
    accentColor: Color,
    onReassignClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(time, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.width(64.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .then(if (onReassignClick != null) Modifier.clickable { onReassignClick() } else Modifier),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, accentColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(subtitle, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                }

                if (badgeIcon != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(badgeIcon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    }
                } else if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(badgeText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyMemberAvatar(name: String, role: String, isOnline: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF13253E))
                    .border(1.5.dp, Color(0xFF00B4D8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = name, tint = Color.White, modifier = Modifier.size(26.dp))
            }

            if (isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2FA860))
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(role, fontSize = 10.sp, color = Color.Gray)
    }
}
