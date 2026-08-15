package com.example.ui.screens.modules

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionManagerScreen(viewModel: PalmViewModel, onBack: () -> Unit) {
    val subscriptions by viewModel.subscriptions.collectAsState()

    var showAddSubDialog by remember { mutableStateOf(false) }
    var subName by remember { mutableStateOf("") }
    var subCost by remember { mutableStateOf("") }
    var subCycle by remember { mutableStateOf("Monthly") }

    val activeSubs = subscriptions.filter { !it.isCancelled }
    val monthlyTotal = activeSubs.sumOf {
        if (it.cycle.equals("Annual", ignoreCase = true)) it.cost / 12.0 else it.cost
    }
    val annualTotal = monthlyTotal * 12.0

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSubDialog = true },
                containerColor = PalmNavy,
                contentColor = PalmWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("add_sub_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subscription")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PalmBackground)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PalmNavy)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Subscription Manager",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmNavy
                    )
                    Text(
                        text = "Digital Media, SaaS & Protection Shield",
                        fontSize = 11.sp,
                        color = PalmLineGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Spend Summary Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PalmNavy),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("TOTAL MONTHLY COMMITMENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PalmLineGrey)
                        Text("₹${String.format("%.2f", monthlyTotal)}/mo", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PalmWhite)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("PROJECTED ANNUAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PalmLineGrey)
                        Text("₹${String.format("%.2f", annualTotal)}/yr", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PalmAlertAmber)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "ACTIVE RECURRING CHARGES (${activeSubs.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PalmLineGrey,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(activeSubs) { sub ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                imageVector = when (sub.logoIcon.lowercase()) {
                                    "car" -> Icons.Default.DirectionsCar
                                    "netflix" -> Icons.Default.Tv
                                    "spotify" -> Icons.Default.MusicNote
                                    "icloud" -> Icons.Default.Cloud
                                    "chatgpt" -> Icons.Default.SmartToy
                                    else -> Icons.Default.Subscriptions
                                },
                                contentDescription = sub.name,
                                tint = PalmAccentBlue,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sub.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PalmNavy
                                )
                                Text(
                                    text = "Renews in ${sub.daysLeft} days (${sub.cycle})",
                                    fontSize = 12.sp,
                                    color = PalmLineGrey
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${String.format("%.2f", sub.cost)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PalmNavy
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                TextButton(
                                    onClick = { viewModel.cancelSubscription(sub.id) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("cancel_sub_${sub.id}")
                                ) {
                                    Text("Mark Cancelled", fontSize = 10.sp, color = PalmAlertRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSubDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubDialog = false },
            title = { Text("Track New Subscription", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text("Subscription Name (e.g. Disney+, Gym)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = subCost,
                        onValueChange = { subCost = it },
                        label = { Text("Cost (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = subCycle == "Monthly",
                            onClick = { subCycle = "Monthly" },
                            label = { Text("Monthly") }
                        )
                        FilterChip(
                            selected = subCycle == "Annual",
                            onClick = { subCycle = "Annual" },
                            label = { Text("Annual") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val costVal = subCost.toDoubleOrNull() ?: 0.0
                        if (subName.isNotBlank() && costVal > 0) {
                            viewModel.addSubscription(subName, costVal, subCycle)
                        }
                        showAddSubDialog = false
                        subName = ""
                        subCost = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PalmNavy)
                ) {
                    Text("Save Subscription")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
