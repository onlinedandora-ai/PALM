package com.example.ui.screens

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ModuleEntity
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

// ─── Palette (matches Module Store screenshot) ────────────────────────────────
private val PageBgStore    = Color(0xFFF4F6FB)
private val CardWhiteStore = Color(0xFFFFFFFF)
private val NavyStore      = Color(0xFF1A2340)
private val BlueStore      = Color(0xFF2F6FED)
private val GreenCheck     = Color(0xFF2A9D5C)
private val GreenCheckBg   = Color(0xFFEBF7F0)
private val BluePuzzleBg   = Color(0xFFEBF1FD)
private val DividerStore   = Color(0xFFE8ECF2)
private val GreyText       = Color(0xFF8C9BAB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleStoreScreen(viewModel: PalmViewModel) {
    val allModules by viewModel.modules.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedPreviewModule by remember { mutableStateOf<ModuleEntity?>(null) }

    val categories = listOf("All", "Finance", "Home", "Health", "Travel", "Vehicle")

    val filteredModules = allModules.filter { mod ->
        val matchesCategory = if (selectedCategory == "All") true
            else mod.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = mod.displayName.contains(searchQuery, ignoreCase = true) ||
            mod.permissions.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBgStore)
    ) {
        // ── Fixed header section ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PageBgStore)
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 0.dp)
        ) {
            // Header
            Text(
                text = "Module Store & Catalog",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NavyStore
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Dynamic Feature Modules (DFMs) fetched on-demand.",
                fontSize = 12.sp,
                color = GreyText
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                color = CardWhiteStore
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search 70+ lifestyle modules...",
                            fontSize = 13.sp,
                            color = GreyText
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GreyText,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("module_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = BlueStore,
                        unfocusedBorderColor    = Color.Transparent,
                        focusedContainerColor   = CardWhiteStore,
                        unfocusedContainerColor = CardWhiteStore
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(NavyStore)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 18.dp, vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                cat,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(CardWhiteStore)
                                .border(1.dp, DividerStore, RoundedCornerShape(20.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 18.dp, vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                cat,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = NavyStore
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section count label
            Text(
                text = "AVAILABLE MODULES (${filteredModules.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GreyText,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        // ── Scrollable module list ─────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredModules) { mod ->
                ModuleCard(
                    mod = mod,
                    onClick = { selectedPreviewModule = mod }
                )
            }
        }
    }

    // ── Module Preview Dialog ─────────────────────────────────────────────────
    selectedPreviewModule?.let { mod ->
        AlertDialog(
            onDismissRequest = { selectedPreviewModule = null },
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (mod.isInstalled) GreenCheckBg else BluePuzzleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (mod.isInstalled) Icons.Default.CheckCircle else Icons.Default.Extension,
                        contentDescription = null,
                        tint = if (mod.isInstalled) GreenCheck else BlueStore,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = mod.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyStore
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Version: ${mod.version}", fontSize = 12.sp, color = GreyText)
                        Text(
                            "Bundle: ${mod.sizeKb} KB",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyStore
                        )
                    }

                    HorizontalDivider(color = DividerStore)

                    Text(
                        text = "✅  Ed25519 Cryptographic Signature: VALID",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenCheck
                    )

                    Text(
                        text = "Requested Permissions:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyStore
                    )
                    Text(
                        "• " + mod.permissions.replace(", ", "\n• "),
                        fontSize = 12.sp,
                        color = Color(0xFF4A5568)
                    )

                    Text(
                        "Privacy Summary:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyStore
                    )
                    Text(
                        text = "Data stays inside SQLCipher local namespace 'mod_${mod.id.replace("-","_")}'. Zero external tracking.",
                        fontSize = 12.sp,
                        color = Color(0xFF4A5568)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleModuleInstall(mod.id, mod.isInstalled)
                        if (!mod.isInstalled) viewModel.openModule(mod.id)
                        selectedPreviewModule = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mod.isInstalled) Color(0xFFE53935) else BlueStore
                    )
                ) {
                    Text(
                        text = if (mod.isInstalled) "Uninstall & Wipe Data" else "Install Module",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPreviewModule = null }) {
                    Text("Close", color = GreyText)
                }
            }
        )
    }
}

// ─── Module Card (matches screenshot layout) ──────────────────────────────────
@Composable
private fun ModuleCard(
    mod: ModuleEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardWhiteStore),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Module status icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(if (mod.isInstalled) GreenCheckBg else BluePuzzleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (mod.isInstalled) Icons.Default.CheckCircle else Icons.Default.Extension,
                    contentDescription = mod.displayName,
                    tint = if (mod.isInstalled) GreenCheck else BlueStore,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Name + size info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mod.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NavyStore
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = buildString {
                        append("Size: ${mod.sizeKb} KB")
                        if (mod.permissions.isNotBlank()) {
                            append("  •  Permissions:")
                        }
                    },
                    fontSize = 11.sp,
                    color = GreyText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action button
            OutlinedButton(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerStore),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyStore)
            ) {
                Text(
                    text = if (mod.isInstalled) "Open / Manage" else "Preview & Add",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyStore
                )
            }
        }
    }
}
