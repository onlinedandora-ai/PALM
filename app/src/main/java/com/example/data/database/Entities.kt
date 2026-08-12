package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mod_dfm_modules")
data class ModuleEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val version: String,
    val sizeKb: Int,
    val category: String, // finance, home, health, travel, vehicle
    val isInstalled: Boolean,
    val entryUrl: String,
    val permissions: String, // comma separated
    val statusText: String, // "Up to date", "Action needed"
    val statusColorHex: String // #2FA860, #F4B73F, etc.
)

@Entity(tableName = "mod_daily_events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_primary",
    val title: String,
    val dayOfWeek: String, // MON, TUE, WED, etc.
    val time: String,
    val assignedTo: String,
    val status: String, // Confirmed, Pending
    val isHandoff: Boolean,
    val notes: String = ""
)

@Entity(tableName = "mod_finance_expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_primary",
    val title: String,
    val category: String, // Groceries, Utilities, Subscriptions, Entertainment, etc.
    val amount: Double,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "mod_finance_budgets")
data class BudgetEntity(
    @PrimaryKey val category: String,
    val userId: String = "user_primary",
    val limitAmount: Double,
    val spentAmount: Double
)

@Entity(tableName = "mod_vehicle_hub")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_primary",
    val name: String, // "2023 Tesla Model Y", "2020 Honda CR-V"
    val licensePlate: String,
    val insuranceExpiryDays: Int,
    val insurancePolicyRef: String,
    val insuranceCost: Double,
    val emissionsNextMonth: String,
    val oilChangeMilesRemaining: Int,
    val isPrimary: Boolean = false
)

@Entity(tableName = "mod_subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_primary",
    val name: String, // Netflix, Spotify, iCloud, ChatGPT Plus
    val cost: Double,
    val cycle: String, // Monthly, Annual
    val renewalDate: String, // "2026-09-01"
    val daysLeft: Int,
    val isCancelled: Boolean = false,
    val logoIcon: String // netflix, spotify, etc.
)

@Entity(tableName = "mod_vault_documents")
data class VaultDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_primary",
    val title: String, // Passport_Sarah.pdf
    val category: String, // Identity Docs, Property & Insurance, Digital Accounts
    val encryptedSize: String, // "1.2 MB"
    val expiryInfo: String, // "Expires Nov 2028"
    val sharingNote: String, // "Shared with Spouse" or "Vault Only"
    val isBiometricProtected: Boolean = true
)

@Entity(tableName = "mod_password_manager")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_primary",
    val accountTitle: String, // "Google Workspace", "Netflix Account"
    val websiteUrl: String, // "https://myaccount.google.com"
    val username: String, // "sarah.connor@gmail.com"
    val encryptedPassword: String, // AES-256 encrypted string
    val category: String, // "Banking", "Social", "Work", "Utilities", "Personal"
    val notes: String = "",
    val totpSecret: String = "",
    val iconName: String = "key",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "mod_sms_fallback_logs")
data class SmsLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "user_primary",
    val recipientName: String,
    val phoneNumber: String,
    val messageText: String,
    val sentTimestamp: Long = System.currentTimeMillis(),
    val status: String // Sent, Confirmed
)
