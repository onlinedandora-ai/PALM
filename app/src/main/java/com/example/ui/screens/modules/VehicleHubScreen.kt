package com.example.ui.screens.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

@Composable
fun VehicleHubScreen(viewModel: PalmViewModel, onBack: () -> Unit) {
    val vehicles by viewModel.vehicles.collectAsState()
    var selectedVehicleIndex by remember { mutableStateOf(0) }

    val activeVehicle = vehicles.getOrNull(selectedVehicleIndex) ?: vehicles.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PalmBackground)
            .padding(16.dp)
    ) {
        // Header
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
                    text = "Vehicle Hub",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PalmNavy
                )
                Text(
                    text = "Household Fleet & Regulatory Maintenance",
                    fontSize = 11.sp,
                    color = PalmLineGrey
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vehicle Selector Carousel / Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            vehicles.forEachIndexed { idx, veh ->
                val isSelected = idx == selectedVehicleIndex
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedVehicleIndex = idx },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PalmNavy else PalmWhite
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PalmNavy else PalmCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = veh.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) PalmWhite else PalmNavy,
                            maxLines = 1
                        )
                        Text(
                            text = "Plate: ${veh.licensePlate}",
                            fontSize = 11.sp,
                            color = if (isSelected) PalmWhite.copy(alpha = 0.7f) else PalmLineGrey
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        activeVehicle?.let { veh ->
            // Insurance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PalmWhite),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = "Insurance", tint = PalmAccentBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Car Insurance Policy",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PalmNavy
                            )
                        }

                        Surface(
                            color = if (veh.insuranceExpiryDays <= 7) PalmAlertAmber.copy(alpha = 0.2f) else PalmSuccessGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Renews in ${veh.insuranceExpiryDays} Days",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (veh.insuranceExpiryDays <= 7) PalmNavy else PalmSuccessGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Policy Ref: ${veh.insurancePolicyRef}", fontSize = 13.sp, color = PalmInk)
                    Text("Annual Premium: ₹${String.format("%.2f", veh.insuranceCost)}", fontSize = 13.sp, color = PalmNavy, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))


                    OutlinedButton(
                        onClick = { viewModel.selectTab(2) }, // Links to Vault tab
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("view_insurance_card_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.FolderZip, contentDescription = "Vault Doc", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Insurance Card PDF (Vault)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PalmNavy)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Regulatory Emissions & Service Tracker
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PalmWhite),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Regulatory & Service Trackers",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmNavy
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Emissions & Inspection", fontSize = 12.sp, color = PalmLineGrey)
                            Text(veh.emissionsNextMonth, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PalmNavy)
                        }

                        Column {
                            Text("Oil / Tire Service", fontSize = 12.sp, color = PalmLineGrey)
                            Text("${veh.oilChangeMilesRemaining} mi remaining", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PalmAccentBlue)
                        }
                    }
                }
            }
        }
    }
}
