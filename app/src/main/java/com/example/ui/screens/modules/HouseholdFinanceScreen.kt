package com.example.ui.screens.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.BudgetEntity
import com.example.data.database.ExpenseEntity
import com.example.data.domain.MasterChecklistCatalog
import com.example.data.domain.MasterChecklistCategory
import com.example.data.domain.MasterChecklistItem
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdFinanceScreen(viewModel: PalmViewModel, onBack: () -> Unit) {
    val budgets by viewModel.budgets.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Finance Dashboard, 1: Master Checklist Explorer

    var showAddExpenseBottomSheet by remember { mutableStateOf(false) }
    var newExpenseTitle by remember { mutableStateOf("") }
    var newExpenseCategory by remember { mutableStateOf("Groceries & Essentials") }
    var newExpenseAmount by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    // Master Checklist state
    var searchQuery by remember { mutableStateOf("") }
    var selectedFrequencyFilter by remember { mutableStateOf("All") }

    val totalSpent = budgets.sumOf { it.spentAmount }
    val totalLimit = budgets.sumOf { it.limitAmount }
    val remaining = (totalLimit - totalSpent).coerceAtLeast(0.0)

    val masterCategories = remember { MasterChecklistCatalog.getAllCategories() }

    val categories = remember(budgets) {
        listOf("All") + (budgets.map { it.category } + masterCategories.map { it.name }).distinct()
    }

    val filteredExpenses = remember(expenses, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") expenses
        else expenses.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExpenseBottomSheet = true },
                containerColor = PalmNavy,
                contentColor = PalmWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("add_expense_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PalmBackground)
                .padding(innerPadding)
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PalmNavy)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Household Finance & Master Checklist",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmNavy
                    )
                    Text(
                        text = "16 Master Expense & Subscription Categories",
                        fontSize = 11.sp,
                        color = PalmLineGrey
                    )
                }

                Surface(
                    color = PalmNavy.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Encrypted", tint = PalmNavy, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AES-256", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PalmNavy)
                    }
                }
            }

            // Tab Selector (Dashboard vs Master Checklist)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = PalmWhite,
                contentColor = PalmNavy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Active Budgets", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Master Checklist (16)", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (selectedTab == 0) {
                // TAB 0: Active Budgets & Finance Dashboard
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Overall Spending Overview Card
                    item {
                        SpendingOverviewCard(
                            totalSpent = totalSpent,
                            totalLimit = totalLimit,
                            remaining = remaining
                        )
                    }

                    // Expense Visualization Chart Card
                    item {
                        ExpenseVisualizationChartCard(budgets = budgets)
                    }

                    // Quick Seed Master Template Action
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PalmNavy.copy(alpha = 0.04f)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PalmNavy.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "17 Master Household Expense Categories",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PalmNavy
                                    )
                                    Text(
                                        text = "Populate & track budgets across all 17 master categories.",
                                        fontSize = 11.sp,
                                        color = PalmLineGrey
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { viewModel.seedAllMasterUseCases() },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Seed All (17)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { selectedTab = 1 },
                                        colors = ButtonDefaults.buttonColors(containerColor = PalmNavy),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Explore", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Category Filter Chips
                    item {
                        Column {
                            Text(
                                text = "MONTHLY BUDGET TRACKING",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PalmLineGrey,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(categories) { category ->
                                    val isSelected = category == selectedCategoryFilter
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCategoryFilter = category },
                                        label = { Text(category, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PalmNavy,
                                            selectedLabelColor = PalmWhite
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Budget Category Cards
                    items(budgets) { budget ->
                        if (selectedCategoryFilter == "All" || budget.category.equals(selectedCategoryFilter, ignoreCase = true)) {
                            BudgetTrackingCard(budget = budget)
                        }
                    }

                    // Recent Transactions Log Section
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "RECENT EXPENSES (${filteredExpenses.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PalmLineGrey,
                            letterSpacing = 1.sp
                        )
                    }

                    if (filteredExpenses.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = PalmWhite),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No expenses recorded yet for this category.", fontSize = 13.sp, color = PalmLineGrey)
                                }
                            }
                        }
                    } else {
                        items(filteredExpenses) { expense ->
                            ExpenseItemRow(expense = expense)
                        }
                    }
                }
            } else {
                // TAB 1: Master Checklist (16 Categories & Sub-items Explorer)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search 16 categories, e.g. Netflix, Daycare, Tax, FASTag...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = PalmWhite, unfocusedContainerColor = PalmWhite),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Frequency Filter Chips
                    val frequencies = listOf("All", "Monthly", "Annual", "Occasional", "Seasonal", "Term-wise")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(frequencies) { freq ->
                            val isSelected = freq == selectedFrequencyFilter
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFrequencyFilter = freq },
                                label = { Text(freq, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PalmNavy,
                                    selectedLabelColor = PalmWhite
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val searchResults = remember(searchQuery) {
                        if (searchQuery.isNotBlank()) MasterChecklistCatalog.searchItems(searchQuery) else emptyList()
                    }

                    if (searchQuery.isNotBlank()) {
                        // Display Search Results
                        Text(
                            text = "SEARCH RESULTS (${searchResults.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PalmLineGrey,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (searchResults.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = PalmWhite),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No matching use-case items found.", fontSize = 13.sp, color = PalmLineGrey)
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(searchResults) { (cat, item) ->
                                    MasterChecklistItemCard(
                                        categoryName = cat.name,
                                        item = item,
                                        onAddToBudget = {
                                            viewModel.addChecklistItemToBudget(item.title, cat.name, item.estimatedAmount)
                                        },
                                        onQuickExpense = {
                                            newExpenseTitle = item.title
                                            newExpenseCategory = cat.name
                                            newExpenseAmount = item.estimatedAmount.toInt().toString()
                                            showAddExpenseBottomSheet = true
                                        },
                                        onAddSubscription = {
                                            viewModel.addChecklistItemToSubscription(item.title, item.estimatedAmount, item.frequency)
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // Display 16 Categories
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(masterCategories) { cat ->
                                MasterCategoryCard(
                                    category = cat,
                                    frequencyFilter = selectedFrequencyFilter,
                                    onAddToBudget = { item ->
                                        viewModel.addChecklistItemToBudget(item.title, cat.name, item.estimatedAmount)
                                    },
                                    onQuickExpense = { item ->
                                        newExpenseTitle = item.title
                                        newExpenseCategory = cat.name
                                        newExpenseAmount = item.estimatedAmount.toInt().toString()
                                        showAddExpenseBottomSheet = true
                                    },
                                    onAddSubscription = { item ->
                                        viewModel.addChecklistItemToSubscription(item.title, item.estimatedAmount, item.frequency)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Quick Expense Entry Dialog
    if (showAddExpenseBottomSheet) {
        AlertDialog(
            onDismissRequest = { showAddExpenseBottomSheet = false },
            title = { Text("Quick Expense Entry", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newExpenseTitle,
                        onValueChange = { newExpenseTitle = it },
                        label = { Text("Expense Title (e.g. Reliance Smart / Daycare)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_title_input")
                    )

                    OutlinedTextField(
                        value = newExpenseCategory,
                        onValueChange = { newExpenseCategory = it },
                        label = { Text("Category (Groceries, Insurance, Utilities, etc.)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newExpenseAmount,
                        onValueChange = { newExpenseAmount = it },
                        label = { Text("Amount (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_amount_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = newExpenseAmount.toDoubleOrNull() ?: 0.0
                        if (newExpenseTitle.isNotBlank() && amt > 0) {
                            viewModel.addExpense(newExpenseTitle, newExpenseCategory, amt)
                        }
                        showAddExpenseBottomSheet = false
                        newExpenseTitle = ""
                        newExpenseAmount = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PalmNavy),
                    modifier = Modifier.testTag("submit_expense_button")
                ) {
                    Text("Add Expense")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExpenseBottomSheet = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ─── MASTER CHECKLIST CATEGORY CARD ─────────────────────────────────────────

@Composable
fun MasterCategoryCard(
    category: MasterChecklistCategory,
    frequencyFilter: String,
    onAddToBudget: (MasterChecklistItem) -> Unit,
    onQuickExpense: (MasterChecklistItem) -> Unit,
    onAddSubscription: (MasterChecklistItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val filteredItems = remember(category.items, frequencyFilter) {
        if (frequencyFilter == "All") category.items
        else category.items.filter { it.frequency.contains(frequencyFilter, ignoreCase = true) }
    }

    if (filteredItems.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PalmWhite),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = PalmNavy.copy(alpha = 0.08f),
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (category.iconName) {
                                "tv" -> Icons.Default.Tv
                                "shield" -> Icons.Default.Shield
                                "home" -> Icons.Default.Home
                                "bank" -> Icons.Default.AccountBalance
                                "health" -> Icons.Default.LocalHospital
                                "lifestyle" -> Icons.Default.SelfImprovement
                                "child" -> Icons.Default.ChildCare
                                "pets" -> Icons.Default.Pets
                                "smart_home" -> Icons.Default.Router
                                "credit_card" -> Icons.Default.CreditCard
                                "work" -> Icons.Default.Work
                                "checkroom" -> Icons.Default.Checkroom
                                "card_giftcard" -> Icons.Default.CardGiftcard
                                "gavel" -> Icons.Default.Gavel
                                "accessible" -> Icons.Default.MedicalServices
                                "build" -> Icons.Default.Build
                                else -> Icons.Default.Category
                            },
                            contentDescription = category.name,
                            tint = PalmNavy,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${category.categoryNumber}. ${category.name}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmNavy
                    )
                    Text(
                        text = "${filteredItems.size} Use-Cases • ${category.defaultFrequency}",
                        fontSize = 11.sp,
                        color = PalmLineGrey
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle",
                    tint = PalmNavy
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = PalmCardBorder)

                    filteredItems.forEach { item ->
                        MasterChecklistItemCard(
                            categoryName = category.name,
                            item = item,
                            onAddToBudget = { onAddToBudget(item) },
                            onQuickExpense = { onQuickExpense(item) },
                            onAddSubscription = { onAddSubscription(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MasterChecklistItemCard(
    categoryName: String,
    item: MasterChecklistItem,
    onAddToBudget: () -> Unit,
    onQuickExpense: () -> Unit,
    onAddSubscription: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PalmSurfaceLight),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmNavy
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = PalmNavy.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = item.frequency,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PalmNavy,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Est. ₹${item.estimatedAmount.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PalmAccentBlue
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.description,
                fontSize = 11.sp,
                color = PalmLineGrey
            )

            if (item.examples.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Examples: " + item.examples.take(4).joinToString(", "),
                    fontSize = 10.sp,
                    color = PalmNavy.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onAddToBudget,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("+ Budget", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                if (item.defaultType == "Subscription" || item.frequency.contains("Monthly", true)) {
                    OutlinedButton(
                        onClick = onAddSubscription,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("+ Sub", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Button(
                    onClick = onQuickExpense,
                    colors = ButtonDefaults.buttonColors(containerColor = PalmNavy),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("+ Log", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SpendingOverviewCard(
    totalSpent: Double,
    totalLimit: Double,
    remaining: Double
) {
    val progress = if (totalLimit > 0) (totalSpent / totalLimit).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(600), label = "progress")
    val pct = (progress * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PalmNavy),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(88.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = PalmAccentBlue,
                    trackColor = PalmWhite.copy(alpha = 0.2f),
                    strokeWidth = 9.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$pct%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmWhite
                    )
                    Text(
                        text = "Used",
                        fontSize = 10.sp,
                        color = PalmWhite.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("MONTHLY BUDGET OVERVIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PalmLineGrey, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Spent: ₹${String.format("%.2f", totalSpent)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PalmWhite)
                Text("Limit: ₹${String.format("%.2f", totalLimit)}", fontSize = 13.sp, color = PalmWhite.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Remaining: ₹${String.format("%.2f", remaining)}",
                    fontSize = 13.sp,
                    color = if (totalSpent > totalLimit) PalmAlertRed else PalmSuccessGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ExpenseVisualizationChartCard(budgets: List<BudgetEntity>) {
    var selectedCategoryIndex by remember { mutableStateOf<Int?>(null) }

    val categoryColors = listOf(
        PalmAccentBlue,
        PalmNavy,
        PalmAlertAmber,
        PalmSuccessGreen,
        Color(0xFF8E44AD),
        Color(0xFFE67E22),
        Color(0xFF00BCD4),
        Color(0xFFE91E63)
    )

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
                    Icon(Icons.Default.BarChart, contentDescription = "Chart", tint = PalmAccentBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Category Budget Breakdown",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmNavy
                    )
                }

                selectedCategoryIndex?.let { idx ->
                    val b = budgets.getOrNull(idx)
                    if (b != null) {
                        Surface(
                            color = PalmNavy.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${b.category.take(12)}: ₹${String.format("%.0f", b.spentAmount)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PalmNavy,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (budgets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No budgets defined.", fontSize = 12.sp, color = PalmLineGrey)
                }
            } else {
                val maxSpent = (budgets.maxOfOrNull { it.spentAmount } ?: 1.0).coerceAtLeast(1.0)
                val maxBarHeight = 110.dp

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val widthPx = constraints.maxWidth.toFloat()
                    val barWidthPx = (widthPx / (budgets.size * 2f)).coerceIn(16f, 40f)

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(maxBarHeight)
                    ) {
                        budgets.forEachIndexed { index, budget ->
                            val barHeight = ((budget.spentAmount / maxSpent) * maxBarHeight.toPx()).toFloat().coerceAtLeast(12.dp.toPx())
                            val x = index * barWidthPx * 2f + (barWidthPx / 2f)
                            val y = maxBarHeight.toPx() - barHeight

                            val color = categoryColors.getOrElse(index % categoryColors.size) { PalmAccentBlue }
                            val isSelected = selectedCategoryIndex == index

                            drawRoundRect(
                                color = if (isSelected) color else color.copy(alpha = 0.85f),
                                topLeft = Offset(x, y),
                                size = Size(barWidthPx, barHeight),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Chart Legend Labels
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(budgets.size) { index ->
                        val budget = budgets[index]
                        val color = categoryColors.getOrElse(index % categoryColors.size) { PalmAccentBlue }
                        val isSelected = selectedCategoryIndex == index

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedCategoryIndex = index }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = budget.category.take(12),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PalmNavy else PalmLineGrey
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetTrackingCard(budget: BudgetEntity) {
    val ratio = if (budget.limitAmount > 0) (budget.spentAmount / budget.limitAmount) else 0.0
    val pct = (ratio * 100).toInt()

    val barColor = when {
        ratio >= 1.0 -> PalmAlertRed
        ratio >= 0.8 -> PalmAlertAmber
        else -> PalmAccentBlue
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PalmWhite),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PalmCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = budget.category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmNavy
                    )

                    if (ratio >= 1.0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = PalmAlertRed.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "OVER BUDGET",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PalmAlertRed,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "₹${String.format("%.0f", budget.spentAmount)} / ₹${String.format("%.0f", budget.limitAmount)} ($pct%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (ratio >= 1.0) PalmAlertRed else PalmNavy
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { ratio.toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = barColor,
                trackColor = PalmSurfaceLight
            )
        }
    }
}

@Composable
fun ExpenseItemRow(expense: ExpenseEntity) {
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
            Surface(
                color = PalmNavy.copy(alpha = 0.08f),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when {
                            expense.category.lowercase().contains("groceries") -> Icons.Default.ShoppingCart
                            expense.category.lowercase().contains("utilities") -> Icons.Default.Bolt
                            expense.category.lowercase().contains("dining") || expense.category.lowercase().contains("food") -> Icons.Default.Restaurant
                            expense.category.lowercase().contains("subscription") || expense.category.lowercase().contains("media") -> Icons.Default.Tv
                            expense.category.lowercase().contains("health") || expense.category.lowercase().contains("pharmacy") -> Icons.Default.LocalHospital
                            expense.category.lowercase().contains("child") || expense.category.lowercase().contains("education") -> Icons.Default.ChildCare
                            expense.category.lowercase().contains("pet") -> Icons.Default.Pets
                            expense.category.lowercase().contains("debt") || expense.category.lowercase().contains("loan") -> Icons.Default.CreditCard
                            else -> Icons.Default.Receipt
                        },
                        contentDescription = expense.category,
                        tint = PalmNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PalmNavy
                )
                Text(
                    text = "${expense.category} • ${expense.date}",
                    fontSize = 11.sp,
                    color = PalmLineGrey
                )
            }

            Text(
                text = "-₹${String.format("%.2f", expense.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PalmNavy
            )
        }
    }
}
