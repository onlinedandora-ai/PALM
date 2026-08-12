package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PalmDao {

    // Dynamic Modules
    @Query("SELECT * FROM mod_dfm_modules")
    fun getAllModules(): Flow<List<ModuleEntity>>

    @Query("SELECT * FROM mod_dfm_modules WHERE isInstalled = 1")
    fun getInstalledModules(): Flow<List<ModuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ModuleEntity>)

    @Query("UPDATE mod_dfm_modules SET isInstalled = :isInstalled WHERE id = :id")
    suspend fun updateModuleInstallation(id: String, isInstalled: Boolean)

    @Query("UPDATE mod_dfm_modules SET statusText = :statusText, statusColorHex = :colorHex WHERE id = :id")
    suspend fun updateModuleStatus(id: String, statusText: String, colorHex: String)

    // Events
    @Query("SELECT * FROM mod_daily_events")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM mod_daily_events WHERE userId = :userId")
    fun getEventsForUser(userId: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("UPDATE mod_daily_events SET assignedTo = :assignedTo, status = :status WHERE id = :id")
    suspend fun updateEventAssignment(id: Long, assignedTo: String, status: String)

    // Finance Expenses & Budgets
    @Query("SELECT * FROM mod_finance_expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM mod_finance_expenses WHERE userId = :userId ORDER BY timestamp DESC")
    fun getExpensesForUser(userId: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM mod_finance_budgets")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM mod_finance_budgets WHERE userId = :userId")
    fun getBudgetsForUser(userId: String): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgets(budgets: List<BudgetEntity>)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    // Vehicles
    @Query("SELECT * FROM mod_vehicle_hub")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM mod_vehicle_hub WHERE userId = :userId")
    fun getVehiclesForUser(userId: String): Flow<List<VehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<VehicleEntity>)

    // Subscriptions
    @Query("SELECT * FROM mod_subscriptions WHERE isCancelled = 0 ORDER BY daysLeft ASC")
    fun getActiveSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM mod_subscriptions ORDER BY daysLeft ASC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM mod_subscriptions WHERE userId = :userId ORDER BY daysLeft ASC")
    fun getSubscriptionsForUser(userId: String): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriptions(subscriptions: List<SubscriptionEntity>)

    @Query("UPDATE mod_subscriptions SET isCancelled = 1 WHERE id = :id")
    suspend fun cancelSubscription(id: Long)

    // Vault Documents
    @Query("SELECT * FROM mod_vault_documents")
    fun getAllVaultDocs(): Flow<List<VaultDocumentEntity>>

    @Query("SELECT * FROM mod_vault_documents WHERE userId = :userId")
    fun getVaultDocsForUser(userId: String): Flow<List<VaultDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultDoc(doc: VaultDocumentEntity)

    @Query("DELETE FROM mod_vault_documents WHERE id = :id")
    suspend fun deleteVaultDoc(id: Long)

    // Password Manager Vault
    @Query("SELECT * FROM mod_password_manager ORDER BY updatedAt DESC")
    fun getAllPasswords(): Flow<List<PasswordEntity>>

    @Query("SELECT * FROM mod_password_manager WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getPasswordsForUser(userId: String): Flow<List<PasswordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPasswords(passwords: List<PasswordEntity>)

    @Query("DELETE FROM mod_password_manager WHERE id = :id")
    suspend fun deletePassword(id: Long)

    // SMS Logs
    @Query("SELECT * FROM mod_sms_fallback_logs ORDER BY sentTimestamp DESC")
    fun getAllSmsLogs(): Flow<List<SmsLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSmsLog(log: SmsLogEntity)

    // Wipe All Data (Settings action)
    @Query("DELETE FROM mod_daily_events")
    suspend fun clearEvents()

    @Query("DELETE FROM mod_finance_expenses")
    suspend fun clearExpenses()

    @Query("DELETE FROM mod_subscriptions")
    suspend fun clearSubscriptions()

    @Query("DELETE FROM mod_vault_documents")
    suspend fun clearVault()

    @Query("DELETE FROM mod_password_manager")
    suspend fun clearPasswords()
}
