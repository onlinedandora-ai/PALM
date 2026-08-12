package com.example.ui.screens.modules

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.BudgetEntity
import com.example.data.database.ExpenseEntity
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdFinanceScreen(viewModel: PalmViewModel, onBack: () -> Unit) {
    val budgets by viewModel.budgets.collectAsState()
    val expenses by viewModel.expenses.collectAsState()

    var showAddExpenseBottomSheet by remember { mutableStateOf(false) }
    var newExpenseTitle by remember { mutableStateOf("") }
    var newExpenseCategory by remember { mutableStateOf("Groceries") }
    var newExpenseAmount by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val totalSpent = budgets.sumOf { it.spentAmount }
    val totalLimit = budgets.sumOf { it.limitAmount }
    val remaining = (totalLimit - totalSpent).coerceAtLeast(0.0)

    val categories = remember(budgets) {
        listOf("All") + budgets.map { it.category }
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
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PalmNavy)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Household Finance Dashboard",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PalmNavy
                    )
                    Text(
                        text = "Monthly Budget Tracking & Spending Insights",
                        fontSize = 11.sp,
                        color = PalmLineGrey
                    )
                }

                Surface(
                    color = PalmNavy.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = "Encrypted", tint = PalmNavy, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Encrypted", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = PalmNavy)
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // 1. Overall Spending Overview Card
                item {
                    SpendingOverviewCard(
                        totalSpent = totalSpent,
                        totalLimit = totalLimit,
                        remaining = remaining
                    )
                }

                // 2. Expense Visualization Chart Card
                item {
                    ExpenseVisualizationChartCard(budgets = budgets)
                }

                // 3. Category Filter Chips
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

                // 4. Budget Category Cards
                items(budgets) { budget ->
                    if (selectedCategoryFilter == "All" || budget.category.equals(selectedCategoryFilter, ignoreCase = true)) {
                        BudgetTrackingCard(budget = budget)
                    }
                }

                // 5. Recent Transactions Log Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
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
                                Text("No expenses recorded yet.", fontSize = 13.sp, color = PalmLineGrey)
                            }
                        }
                    }
                } else {
                    items(filteredExpenses) { expense ->
                        ExpenseItemRow(expense = expense)
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
                        label = { Text("Expense Title (e.g. Supermarket)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expense_title_input")
                    )

                    OutlinedTextField(
                        value = newExpenseCategory,
                        onValueChange = { newExpenseCategory = it },
                        label = { Text("Category (Groceries, Utilities, etc.)") },
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
                Text("OCTOBER BUDGET OVERVIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PalmLineGrey, letterSpacing = 0.5.sp)
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
        Color(0xFFE67E22)
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
                        text = "Expense Category Chart",
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
                                text = "${b.category}: ₹${String.format("%.0f", b.spentAmount)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PalmNavy,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No category data available.", fontSize = 12.sp, color = PalmLineGrey)
                }
            } else {
                val maxSpent = (budgets.maxOfOrNull { it.spentAmount } ?: 1.0).coerceAtLeast(1.0)

                // Canvas Bar Chart
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .pointerInput(budgets) {
                            detectTapGestures { offset ->
                                val barWidth = size.width / (budgets.size * 2 - 1)
                                val index = (offset.x / (barWidth * 2)).toInt()
                                if (index in budgets.indices) {
                                    selectedCategoryIndex = index
                                }
                            }
                        }
                ) {
                    val barWidthPx = size.width / (budgets.size * 2f - 1f)
                    val maxBarHeight = size.height - 30.dp.toPx()

                    // Draw Horizontal Grid Lines
                    for (i in 1..3) {
                        val y = maxBarHeight * (1f - i / 3f)
                        drawLine(
                            color = PalmCardBorder,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    budgets.forEachIndexed { index, budget ->
                        val barHeight = ((budget.spentAmount / maxSpent) * maxBarHeight).toFloat().coerceAtLeast(12.dp.toPx())
                        val x = index * barWidthPx * 2f
                        val y = maxBarHeight - barHeight

                        val color = categoryColors.getOrElse(index) { PalmAccentBlue }
                        val isSelected = selectedCategoryIndex == index

                        // Bar background
                        drawRoundRect(
                            color = if (isSelected) color else color.copy(alpha = 0.85f),
                            topLeft = Offset(x, y),
                            size = Size(barWidthPx, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chart Legend Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    budgets.forEachIndexed { index, budget ->
                        val color = categoryColors.getOrElse(index) { PalmAccentBlue }
                        val isSelected = selectedCategoryIndex == index

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedCategoryIndex = index }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = budget.category.take(6),
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
                        imageVector = when (expense.category.lowercase()) {
                            "groceries" -> Icons.Default.ShoppingCart
                            "utilities" -> Icons.Default.Bolt
                            "dining" -> Icons.Default.Restaurant
                            "entertainment" -> Icons.Default.Movie
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


