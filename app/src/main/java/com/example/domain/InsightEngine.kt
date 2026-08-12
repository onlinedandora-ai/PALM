package com.example.data.domain

import com.example.data.database.BudgetEntity
import com.example.data.database.SubscriptionEntity
import com.example.data.database.VehicleEntity

data class InsightCard(
    val id: String,
    val moduleName: String, // "Vehicle Hub", "Subscriptions", "Household Finance"
    val title: String,
    val description: String,
    val badgeText: String,
    val badgeColorHex: String,
    val actionText: String = "View Details",
    val route: String
)

object InsightEngine {

    fun generateInsights(
        vehicles: List<VehicleEntity>,
        subscriptions: List<SubscriptionEntity>,
        budgets: List<BudgetEntity>
    ): List<InsightCard> {
        val insights = mutableListOf<InsightCard>()

        // 1. Vehicle Insurance Renewal Check
        vehicles.firstOrNull { it.insuranceExpiryDays <= 7 }?.let { vehicle ->
            insights.add(
                InsightCard(
                    id = "vehicle-insurance-renew",
                    moduleName = "Vehicle Hub",
                    title = "${vehicle.name} Insurance Renewal",
                    description = "Car insurance renews in ${vehicle.insuranceExpiryDays} days (₹${String.format("%.2f", vehicle.insuranceCost)}).",
                    badgeText = "Urgent Renewal",
                    badgeColorHex = "#F4B73F",
                    actionText = "View Insurance",
                    route = "vehicle-hub"
                )
            )
        }

        // 2. Over-budget or high warning budget check
        budgets.firstOrNull { it.spentAmount >= it.limitAmount }?.let { budget ->
            insights.add(
                InsightCard(
                    id = "budget-over-limit",
                    moduleName = "Household Finance",
                    title = "${budget.category} Over Budget",
                    description = "Spent ₹${String.format("%.2f", budget.spentAmount)} of ₹${String.format("%.2f", budget.limitAmount)} limit (100%).",
                    badgeText = "Budget Alert",
                    badgeColorHex = "#E53935",
                    actionText = "Review Budget",
                    route = "household-finance"
                )
            )
        } ?: budgets.firstOrNull { (it.spentAmount / it.limitAmount) >= 0.80 }?.let { budget ->
            val pct = ((budget.spentAmount / budget.limitAmount) * 100).toInt()
            insights.add(
                InsightCard(
                    id = "budget-warning",
                    moduleName = "Household Finance",
                    title = "${budget.category} Budget Alert",
                    description = "Spent ₹${String.format("%.2f", budget.spentAmount)} of ₹${String.format("%.2f", budget.limitAmount)} limit ($pct%).",
                    badgeText = "$pct% Spent",
                    badgeColorHex = "#2F6FED",
                    actionText = "Adjust Budget",
                    route = "household-finance"
                )
            )
        }

        // 3. Subscription Rate Change / Renewal Check
        subscriptions.firstOrNull { it.daysLeft in 1..15 }?.let { sub ->
            insights.add(
                InsightCard(
                    id = "sub-renewal",
                    moduleName = "Subscriptions",
                    title = "${sub.name} Rate Notice",
                    description = "Auto-renews in ${sub.daysLeft} days (₹${String.format("%.2f", sub.cost)}/${sub.cycle.lowercase()}).",

                    badgeText = "Auto-Renew",
                    badgeColorHex = "#2F6FED",
                    actionText = "Manage Sub",
                    route = "subscriptions"
                )
            )
        }

        // 4. Default fallback insight if list is sparse
        if (insights.size < 2) {
            insights.add(
                InsightCard(
                    id = "vault-security-check",
                    moduleName = "Digital Vault",
                    title = "Biometric Vault Security",
                    description = "AES-256 encrypted sandbox active. 5 secure documents protected on-device.",
                    badgeText = "Secured",
                    badgeColorHex = "#2FA860",
                    actionText = "Open Vault",
                    route = "vault"
                )
            )
        }

        return insights.take(4)
    }
}
