package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.*
import com.example.ui.screens.modules.*
import com.example.ui.theme.*

@Composable
fun PalmMainApp(viewModel: PalmViewModel) {
    val onboardingStep by viewModel.onboardingStep.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val activeModuleScreen by viewModel.activeModuleScreen.collectAsState()

    when (onboardingStep) {
        OnboardingStep.SPLASH_AUTH -> {
            AuthScreen(viewModel = viewModel)
        }

        OnboardingStep.MODULE_SELECTION, OnboardingStep.INITIALIZING_DOWNLOAD -> {
            SetupWizardScreen(viewModel = viewModel)
        }

        OnboardingStep.COMPLETED -> {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = PalmWhite,
                        contentColor = PalmNavy
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0 && activeModuleScreen == null,
                            onClick = { viewModel.selectTab(0) },
                            icon = {
                                Icon(
                                    if (selectedTab == 0) Icons.Default.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text("Home") },
                            modifier = Modifier.testTag("tab_home")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 1 && activeModuleScreen == null,
                            onClick = { viewModel.selectTab(1) },
                            icon = {
                                Icon(
                                    if (selectedTab == 1) Icons.Default.Extension else Icons.Outlined.Extension,
                                    contentDescription = "Modules"
                                )
                            },
                            label = { Text("Modules") },
                            modifier = Modifier.testTag("tab_modules")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 2 && activeModuleScreen == null,
                            onClick = { viewModel.selectTab(2) },
                            icon = {
                                Icon(
                                    if (selectedTab == 2) Icons.Default.Lock else Icons.Outlined.Lock,
                                    contentDescription = "Vault"
                                )
                            },
                            label = { Text("Vault") },
                            modifier = Modifier.testTag("tab_vault")
                        )

                        NavigationBarItem(
                            selected = selectedTab == 3 && activeModuleScreen == null,
                            onClick = { viewModel.selectTab(3) },
                            icon = {
                                Icon(
                                    if (selectedTab == 3) Icons.Default.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text("Settings") },
                            modifier = Modifier.testTag("tab_settings")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (activeModuleScreen != null) {
                        when (activeModuleScreen) {
                            "daily-sync" -> DailySyncScreen(viewModel = viewModel, onBack = { viewModel.closeModule() })
                            "household-finance" -> HouseholdFinanceScreen(viewModel = viewModel, onBack = { viewModel.closeModule() })
                            "vehicle-hub" -> VehicleHubScreen(viewModel = viewModel, onBack = { viewModel.closeModule() })
                            "subscriptions" -> SubscriptionManagerScreen(viewModel = viewModel, onBack = { viewModel.closeModule() })
                            "digital-vault", "vault" -> VaultScreen(viewModel = viewModel)
                            "password-manager" -> PasswordManagerScreen(viewModel = viewModel, onBack = { viewModel.closeModule() })
                            else -> ModuleStoreScreen(viewModel = viewModel)

                        }
                    } else {
                        when (selectedTab) {
                            0 -> HomeDashboardScreen(viewModel = viewModel)
                            1 -> ModuleStoreScreen(viewModel = viewModel)
                            2 -> VaultScreen(viewModel = viewModel)
                            3 -> SettingsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
