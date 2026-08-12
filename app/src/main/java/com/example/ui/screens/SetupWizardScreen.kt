package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.ui.OnboardingStep
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

@Composable
fun SetupWizardScreen(viewModel: PalmViewModel) {
    val onboardingStep by viewModel.onboardingStep.collectAsState()
    val allModules by viewModel.modules.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()

    var selectedModuleIds by remember {
        mutableStateOf(
            setOf("daily-sync", "household-finance", "vehicle-hub", "subscriptions", "digital-vault")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PalmBackground)
            .padding(20.dp)
    ) {
        when (onboardingStep) {
            OnboardingStep.MODULE_SELECTION -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Step 2 of 3",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PalmAccentBlue
                            )
                            Text(
                                text = "Build Your PALM System",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PalmNavy
                            )
                        }

                        TextButton(onClick = { viewModel.startModuleInitialization() }) {
                            Text("Skip for now", color = PalmLineGrey, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Pick 3 or more modules. You can add or remove anytime.",
                        fontSize = 14.sp,
                        color = PalmInk.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(allModules) { mod ->
                            val isChecked = selectedModuleIds.contains(mod.id)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedModuleIds = if (isChecked) {
                                            selectedModuleIds - mod.id
                                        } else {
                                            selectedModuleIds + mod.id
                                        }
                                        viewModel.toggleModuleInstall(mod.id, !isChecked)
                                    }
                                    .border(
                                        width = if (isChecked) 2.dp else 1.dp,
                                        color = if (isChecked) PalmAccentBlue else PalmCardBorder,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isChecked) PalmAccentBlue.copy(alpha = 0.05f) else PalmWhite
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = {
                                                selectedModuleIds = if (it) selectedModuleIds + mod.id else selectedModuleIds - mod.id
                                                viewModel.toggleModuleInstall(mod.id, !it)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = PalmAccentBlue)
                                        )

                                        Badge(
                                            containerColor = PalmSurfaceLight,
                                            contentColor = PalmNavy
                                        ) {
                                            Text("${mod.sizeKb} KB", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = mod.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PalmNavy
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = mod.permissions,
                                        fontSize = 11.sp,
                                        color = PalmLineGrey,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.startModuleInitialization() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("continue_setup_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PalmNavy),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Continue (${selectedModuleIds.size} Selected)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            OnboardingStep.INITIALIZING_DOWNLOAD -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = "Downloading",
                        tint = PalmAccentBlue,
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Setting Up Dynamic Modules...",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmNavy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Fetching Hermes bytecode chunks over CDN & verifying Ed25519 cryptographic signatures.",
                        fontSize = 13.sp,
                        color = PalmLineGrey,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    LinearProgressIndicator(
                        progress = { downloadProgress },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = PalmAccentBlue,
                        trackColor = PalmCardBorder
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${(downloadProgress * 100).toInt()}% Initialized",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PalmNavy
                    )
                }
            }

            else -> {}
        }
    }
}
