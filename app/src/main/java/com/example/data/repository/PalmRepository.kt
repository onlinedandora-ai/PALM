package com.example.data.repository

import com.example.data.database.*
import com.example.util.PasswordEncryptionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PalmRepository(private val dao: PalmDao) {

    val allModules: Flow<List<ModuleEntity>> = dao.getAllModules()
    val installedModules: Flow<List<ModuleEntity>> = dao.getInstalledModules()
    val allEvents: Flow<List<EventEntity>> = dao.getAllEvents()
    val allExpenses: Flow<List<ExpenseEntity>> = dao.getAllExpenses()
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()
    val allVehicles: Flow<List<VehicleEntity>> = dao.getAllVehicles()
    val activeSubscriptions: Flow<List<SubscriptionEntity>> = dao.getActiveSubscriptions()
    val allSubscriptions: Flow<List<SubscriptionEntity>> = dao.getAllSubscriptions()
    val allVaultDocs: Flow<List<VaultDocumentEntity>> = dao.getAllVaultDocs()
    val allPasswords: Flow<List<PasswordEntity>> = dao.getAllPasswords()
    val allSmsLogs: Flow<List<SmsLogEntity>> = dao.getAllSmsLogs()

    fun getPasswordsForUser(userId: String): Flow<List<PasswordEntity>> = dao.getPasswordsForUser(userId)
    fun getVehiclesForUser(userId: String): Flow<List<VehicleEntity>> = dao.getVehiclesForUser(userId)
    fun getSubscriptionsForUser(userId: String): Flow<List<SubscriptionEntity>> = dao.getSubscriptionsForUser(userId)
    fun getBudgetsForUser(userId: String): Flow<List<BudgetEntity>> = dao.getBudgetsForUser(userId)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        val currentModules = dao.getAllModules().first()
        if (currentModules.isEmpty()) {
            dao.insertModules(
                listOf(
                    ModuleEntity(
                        id = "daily-sync",
                        displayName = "Daily & Family Sync",
                        version = "2.1.0",
                        sizeKb = 520,
                        category = "home",
                        isInstalled = true,
                        entryUrl = "https://cdn.palm.internal/modules/daily-sync/2.1.0/main.chunk.js",
                        permissions = "Storage, Notifications, SMS",
                        statusText = "Up to date",
                        statusColorHex = "#2FA860"
                    ),
                    ModuleEntity(
                        id = "household-finance",
                        displayName = "Household Finance",
                        version = "1.4.0",
                        sizeKb = 640,
                        category = "finance",
                        isInstalled = true,
                        entryUrl = "https://cdn.palm.internal/modules/household-finance/1.4.0/main.chunk.js",
                        permissions = "Local Storage, Notifications",
                        statusText = "Action needed",
                        statusColorHex = "#F4B73F"
                    ),
                    ModuleEntity(
                        id = "vehicle-hub",
                        displayName = "Vehicle Hub",
                        version = "1.8.2",
                        sizeKb = 890,
                        category = "vehicle",
                        isInstalled = true,
                        entryUrl = "https://cdn.palm.internal/modules/vehicle-hub/1.8.2/main.chunk.js",
                        permissions = "Storage, Vault Access",
                        statusText = "Up to date",
                        statusColorHex = "#2FA860"
                    ),
                    ModuleEntity(
                        id = "subscriptions",
                        displayName = "Subscriptions",
                        version = "1.2.5",
                        sizeKb = 480,
                        category = "finance",
                        isInstalled = true,
                        entryUrl = "https://cdn.palm.internal/modules/subscriptions/1.2.5/main.chunk.js",
                        permissions = "Local Storage, Notifications",
                        statusText = "Up to date",
                        statusColorHex = "#2FA860"
                    ),
                    ModuleEntity(
                        id = "digital-vault",
                        displayName = "Digital Vault",
                        version = "2.0.1",
                        sizeKb = 950,
                        category = "home",
                        isInstalled = true,
                        entryUrl = "https://cdn.palm.internal/modules/digital-vault/2.0.1/main.chunk.js",
                        permissions = "Keychain Access, Camera, Storage",
                        statusText = "Up to date",
                        statusColorHex = "#2FA860"
                    ),
                    ModuleEntity(
                        id = "password-manager",
                        displayName = "Password Vault & Manager",
                        version = "2.2.0",
                        sizeKb = 780,
                        category = "security",
                        isInstalled = true,
                        entryUrl = "https://cdn.palm.internal/modules/password-manager/2.2.0/main.chunk.js",
                        permissions = "AES Encryption, Clipboard, Supabase Sync",
                        statusText = "Protected",
                        statusColorHex = "#2FA860"
                    ),
                    ModuleEntity(
                        id = "healthcare",
                        displayName = "Healthcare & Vitals",
                        version = "1.0.0",
                        sizeKb = 820,
                        category = "health",
                        isInstalled = false,
                        entryUrl = "https://cdn.palm.internal/modules/healthcare/1.0.0/main.chunk.js",
                        permissions = "Storage, Reminders",
                        statusText = "Not Installed",
                        statusColorHex = "#9AA1AB"
                    ),
                    ModuleEntity(
                        id = "trip-logistics",
                        displayName = "Trip & Travel Logistics",
                        version = "1.1.0",
                        sizeKb = 1100,
                        category = "travel",
                        isInstalled = false,
                        entryUrl = "https://cdn.palm.internal/modules/trip-logistics/1.1.0/main.chunk.js",
                        permissions = "Storage, Location",
                        statusText = "Not Installed",
                        statusColorHex = "#9AA1AB"
                    )
                )
            )
        }

        val currentEvents = dao.getAllEvents().first()
        if (currentEvents.isEmpty()) {
            dao.insertEvent(EventEntity(title = "School Drop-off (Dad)", dayOfWeek = "MON 13", time = "08:00", assignedTo = "Dad", status = "Confirmed", isHandoff = false))
            dao.insertEvent(EventEntity(title = "Soccer Practice Handoff (Mom)", dayOfWeek = "MON 13", time = "15:30", assignedTo = "Mom", status = "Confirmed", isHandoff = true))
            dao.insertEvent(EventEntity(title = "Pediatric Appointment", dayOfWeek = "TUE 14", time = "10:00", assignedTo = "Sarah", status = "Confirmed", isHandoff = false))
            dao.insertEvent(EventEntity(title = "School Pickup Today", dayOfWeek = "TUE 14", time = "15:30", assignedTo = "Alex (Dad)", status = "Confirmed", isHandoff = true))
        }

        val currentBudgets = dao.getAllBudgets().first()
        if (currentBudgets.isEmpty()) {
            populateAllMasterUseCases()
        }

        val currentVehicles = dao.getAllVehicles().first()
        if (currentVehicles.isEmpty()) {
            dao.insertVehicles(
                listOf(
                    VehicleEntity(
                        name = "2023 Tesla Model Y",
                        licensePlate = "7XYZ99",
                        insuranceExpiryDays = 5,
                        insurancePolicyRef = "POL-98234-TES",
                        insuranceCost = 52000.0,
                        emissionsNextMonth = "Nov 2026",
                        oilChangeMilesRemaining = 2400,
                        isPrimary = true
                    ),
                    VehicleEntity(
                        name = "2020 Honda CR-V",
                        licensePlate = "5ABC12",
                        insuranceExpiryDays = 120,
                        insurancePolicyRef = "POL-11204-HON",
                        insuranceCost = 38000.0,
                        emissionsNextMonth = "Feb 2027",
                        oilChangeMilesRemaining = 800,
                        isPrimary = false
                    )
                )
            )
        }

        val currentSubs = dao.getAllSubscriptions().first()
        if (currentSubs.isEmpty()) {
            dao.insertSubscriptions(
                listOf(
                    SubscriptionEntity(name = "Car Insurance Renewal", cost = 52000.00, cycle = "Annual", renewalDate = "5 days", daysLeft = 5, logoIcon = "car"),
                    SubscriptionEntity(name = "Netflix 4K Premium", cost = 649.00, cycle = "Monthly", renewalDate = "Next month", daysLeft = 14, logoIcon = "netflix"),
                    SubscriptionEntity(name = "Spotify Family Plan", cost = 179.00, cycle = "Monthly", renewalDate = "In 18 days", daysLeft = 18, logoIcon = "spotify"),
                    SubscriptionEntity(name = "iCloud 2TB Storage", cost = 219.00, cycle = "Monthly", renewalDate = "In 22 days", daysLeft = 22, logoIcon = "icloud"),
                    SubscriptionEntity(name = "ChatGPT Plus", cost = 1999.00, cycle = "Monthly", renewalDate = "In 28 days", daysLeft = 28, logoIcon = "chatgpt")
                )
            )
        }


        val currentVault = dao.getAllVaultDocs().first()
        if (currentVault.isEmpty()) {
            dao.insertVaultDoc(VaultDocumentEntity(title = "Passport_Sarah.pdf", category = "Identity Docs", encryptedSize = "1.8 MB", expiryInfo = "Expires Nov 2028", sharingNote = "Shared with Spouse"))
            dao.insertVaultDoc(VaultDocumentEntity(title = "House_Deed_Final.pdf", category = "Property & Insurance", encryptedSize = "4.2 MB", expiryInfo = "Encrypted Vault", sharingNote = "Vault Only"))
            dao.insertVaultDoc(VaultDocumentEntity(title = "Tesla_Insurance_Card_2026.pdf", category = "Property & Insurance", encryptedSize = "850 KB", expiryInfo = "Renews in 5 days", sharingNote = "Vehicle Hub Linked"))
        }

        val currentPasswords = dao.getAllPasswords().first()
        if (currentPasswords.isEmpty()) {
            dao.insertPasswords(
                listOf(
                    PasswordEntity(
                        accountTitle = "Google Workspace Primary",
                        websiteUrl = "https://myaccount.google.com",
                        username = "sarah.connor@gmail.com",
                        encryptedPassword = PasswordEncryptionHelper.encrypt("P@ssw0rd!2026#Secure"),
                        category = "Work",
                        notes = "2FA enabled with Authenticator"
                    ),
                    PasswordEntity(
                        accountTitle = "Chase Banking Online",
                        websiteUrl = "https://www.chase.com",
                        username = "sarah_banking_99",
                        encryptedPassword = PasswordEncryptionHelper.encrypt("Bank$99#UltraSafe!"),
                        category = "Banking",
                        notes = "Primary checking & credit account"
                    ),
                    PasswordEntity(
                        accountTitle = "Netflix 4K Account",
                        websiteUrl = "https://www.netflix.com",
                        username = "family.connor@netflix.com",
                        encryptedPassword = PasswordEncryptionHelper.encrypt("StreamFlix#8821"),
                        category = "Personal",
                        notes = "Shared with family"
                    )
                )
            )
        }
    }

    // Module actions
    suspend fun setModuleInstalled(id: String, installed: Boolean) {
        dao.updateModuleInstallation(id, installed)
    }

    // Event actions
    suspend fun reassignEvent(id: Long, newAssignee: String, phone: String, eventTitle: String) {
        dao.updateEventAssignment(id, newAssignee, "Confirmed via SMS")
        val smsMsg = "PALM Alert: Sarah reassigned '$eventTitle' to $newAssignee. Reply YES to confirm."
        dao.insertSmsLog(
            SmsLogEntity(
                recipientName = newAssignee,
                phoneNumber = phone,
                messageText = smsMsg,
                status = "Sent & Confirmed"
            )
        )
    }

    // Expense actions
    suspend fun addExpense(title: String, category: String, amount: Double) {
        dao.insertExpense(ExpenseEntity(title = title, category = category, amount = amount, date = "Today"))
        val budgets = dao.getAllBudgets().first()
        val existing = budgets.find { it.category.equals(category, ignoreCase = true) }
        if (existing != null) {
            val updated = existing.copy(spentAmount = existing.spentAmount + amount)
            dao.updateBudget(updated)
            if (updated.spentAmount >= updated.limitAmount) {
                dao.updateModuleStatus("household-finance", "Over Limit", "#E53935")
            }
        }
    }

    // Subscription actions
    suspend fun cancelSubscription(id: Long) {
        dao.cancelSubscription(id)
    }

    suspend fun addSubscription(name: String, cost: Double, cycle: String, logoIcon: String = "sub") {
        dao.insertSubscriptions(
            listOf(
                SubscriptionEntity(
                    name = name,
                    cost = cost,
                    cycle = cycle,
                    renewalDate = "In 30 days",
                    daysLeft = 30,
                    logoIcon = logoIcon
                )
            )
        )
    }

    suspend fun addOrUpdateBudget(category: String, limitAmount: Double) {
        val currentBudgets = dao.getAllBudgets().first()
        val existing = currentBudgets.find { it.category.equals(category, ignoreCase = true) }
        if (existing != null) {
            dao.updateBudget(existing.copy(limitAmount = limitAmount))
        } else {
            dao.insertBudgets(listOf(BudgetEntity(category = category, limitAmount = limitAmount, spentAmount = 0.0)))
        }
    }

    // Vault actions
    suspend fun addVaultDocument(title: String, category: String) {
        dao.insertVaultDoc(
            VaultDocumentEntity(
                title = title,
                category = category,
                encryptedSize = "1.1 MB",
                expiryInfo = "Encrypted Vault",
                sharingNote = "Vault Only"
            )
        )
    }

    // Password actions
    suspend fun addPassword(passwordEntity: PasswordEntity) {
        dao.insertPassword(passwordEntity)
    }

    suspend fun deletePassword(id: Long) {
        dao.deletePassword(id)
    }

    suspend fun wipeAllData() {
        dao.clearEvents()
        dao.clearExpenses()
        dao.clearSubscriptions()
        dao.clearVault()
        dao.clearPasswords()
    }

    suspend fun populateAllMasterUseCases() {
        val masterBudgets = listOf(
            BudgetEntity(category = "Subscriptions (Digital & Media)", limitAmount = 5000.0, spentAmount = 2496.0),
            BudgetEntity(category = "Insurance Policies", limitAmount = 30000.0, spentAmount = 25000.0),
            BudgetEntity(category = "Household Operations & Utilities", limitAmount = 20000.0, spentAmount = 14500.0),
            BudgetEntity(category = "Financial & Asset Management", limitAmount = 45000.0, spentAmount = 43500.0),
            BudgetEntity(category = "Health & Education", limitAmount = 15000.0, spentAmount = 11000.0),
            BudgetEntity(category = "Leisure, Personal & Lifestyle", limitAmount = 10000.0, spentAmount = 6500.0),
            BudgetEntity(category = "Childcare & Dependent Care", limitAmount = 15000.0, spentAmount = 11500.0),
            BudgetEntity(category = "Pet Care & Expenses", limitAmount = 6000.0, spentAmount = 3800.0),
            BudgetEntity(category = "Smart Home, Security & Tech", limitAmount = 8000.0, spentAmount = 4200.0),
            BudgetEntity(category = "Debt & Loan Obligations", limitAmount = 25000.0, spentAmount = 22500.0),
            BudgetEntity(category = "Remote Work & Professional", limitAmount = 7000.0, spentAmount = 4500.0),
            BudgetEntity(category = "Apparel, Wardrobe & Textiles", limitAmount = 6000.0, spentAmount = 3600.0),
            BudgetEntity(category = "Gifting, Festivities & Giving", limitAmount = 10000.0, spentAmount = 5000.0),
            BudgetEntity(category = "Banking, Tax & Legal Admin", limitAmount = 6000.0, spentAmount = 3500.0),
            BudgetEntity(category = "Elderly Care & Senior Health", limitAmount = 20000.0, spentAmount = 12000.0),
            BudgetEntity(category = "Seasonal & Outdoor Maintenance", limitAmount = 5000.0, spentAmount = 3000.0),
            BudgetEntity(category = "Contingency & Emergency Reserves", limitAmount = 15000.0, spentAmount = 10000.0)
        )
        dao.insertBudgets(masterBudgets)

        val masterExpenses = listOf(
            // 1. Digital Subscriptions & Media
            ExpenseEntity(title = "Netflix 4K Premium", category = "Subscriptions (Digital & Media)", amount = 649.00, date = "Oct 12"),
            ExpenseEntity(title = "Spotify Family Plan", category = "Subscriptions (Digital & Media)", amount = 179.00, date = "Oct 10"),
            ExpenseEntity(title = "Google One 2TB Cloud Storage", category = "Subscriptions (Digital & Media)", amount = 219.00, date = "Oct 08"),
            ExpenseEntity(title = "Duolingo Super Annual", category = "Subscriptions (Digital & Media)", amount = 399.00, date = "Oct 05"),
            ExpenseEntity(title = "PlayStation Plus Deluxe", category = "Subscriptions (Digital & Media)", amount = 499.00, date = "Oct 02"),
            
            // 2. Insurances
            ExpenseEntity(title = "Family Health Floater Cover", category = "Insurance Policies", amount = 15000.00, date = "Oct 01"),
            ExpenseEntity(title = "Term Life Insurance Policy", category = "Insurance Policies", amount = 10000.00, date = "Oct 01"),
            
            // 3. Household Operations & Utilities
            ExpenseEntity(title = "Reliance Smart Pantry & Produce", category = "Household Operations & Utilities", amount = 6500.00, date = "Oct 12"),
            ExpenseEntity(title = "Housekeeper & Cook Salary", category = "Household Operations & Utilities", amount = 8000.00, date = "Oct 01"),
            ExpenseEntity(title = "Electricity & Fiber Internet", category = "Household Operations & Utilities", amount = 3500.00, date = "Oct 05"),

            // 4. Financial & Asset Management
            ExpenseEntity(title = "Apartment Rent Payment", category = "Financial & Asset Management", amount = 35000.00, date = "Oct 01"),
            ExpenseEntity(title = "Fuel & FASTag Toll Pass", category = "Financial & Asset Management", amount = 4500.00, date = "Oct 09"),
            ExpenseEntity(title = "Mutual Fund Nifty Index SIP", category = "Financial & Asset Management", amount = 4000.00, date = "Oct 05"),

            // 5. Health & Education
            ExpenseEntity(title = "School Tuition & Books", category = "Health & Education", amount = 8500.00, date = "Oct 02"),
            ExpenseEntity(title = "Doctor Checkup & Medicines", category = "Health & Education", amount = 2500.00, date = "Oct 07"),

            // 6. Leisure & Lifestyle
            ExpenseEntity(title = "Weekend Dining & UberEats", category = "Leisure, Personal & Lifestyle", amount = 3800.00, date = "Oct 11"),
            ExpenseEntity(title = "Salon & Skincare Grooming", category = "Leisure, Personal & Lifestyle", amount = 2700.00, date = "Oct 08"),

            // 7. Childcare
            ExpenseEntity(title = "Daycare Center Fee", category = "Childcare & Dependent Care", amount = 8000.00, date = "Oct 01"),
            ExpenseEntity(title = "Diapers & Baby Formula", category = "Childcare & Dependent Care", amount = 3500.00, date = "Oct 06"),

            // 8. Pets
            ExpenseEntity(title = "Royal Canin Pet Food & Treats", category = "Pet Care & Expenses", amount = 2500.00, date = "Oct 04"),
            ExpenseEntity(title = "Annual Vet Vaccination", category = "Pet Care & Expenses", amount = 1300.00, date = "Oct 09"),

            // 9. Smart Home & Tech
            ExpenseEntity(title = "Ring Protect Security Plan", category = "Smart Home, Security & Tech", amount = 1200.00, date = "Oct 03"),
            ExpenseEntity(title = "UPS Battery Backup & Charger", category = "Smart Home, Security & Tech", amount = 3000.00, date = "Oct 10"),

            // 10. Debt & Loans
            ExpenseEntity(title = "Chase Credit Card Statement", category = "Debt & Loan Obligations", amount = 15000.00, date = "Oct 02"),
            ExpenseEntity(title = "Student Loan Monthly EMI", category = "Debt & Loan Obligations", amount = 7500.00, date = "Oct 04"),

            // 11. Remote Work & Professional
            ExpenseEntity(title = "Adobe Creative Cloud & Notion", category = "Remote Work & Professional", amount = 2500.00, date = "Oct 05"),
            ExpenseEntity(title = "LinkedIn Premium Renewal", category = "Remote Work & Professional", amount = 2000.00, date = "Oct 07"),

            // 12. Wardrobe & Textiles
            ExpenseEntity(title = "Workwear Apparel & Dry Cleaning", category = "Apparel, Wardrobe & Textiles", amount = 3600.00, date = "Oct 09"),

            // 13. Gifting & Giving
            ExpenseEntity(title = "Festival Holiday Sweets & Tithes", category = "Gifting, Festivities & Giving", amount = 5000.00, date = "Oct 10"),

            // 14. Banking, Tax & Legal
            ExpenseEntity(title = "CPA Tax Prep & TurboTax", category = "Banking, Tax & Legal Admin", amount = 3500.00, date = "Oct 06"),

            // 15. Elderly Care
            ExpenseEntity(title = "Home Caregiver & Physical Therapy", category = "Elderly Care & Senior Health", amount = 12000.00, date = "Oct 01"),

            // 16. Seasonal Prep
            ExpenseEntity(title = "Quarterly Lawn & Pest Control", category = "Seasonal & Outdoor Maintenance", amount = 3000.00, date = "Oct 04"),

            // 17. Emergency Reserves
            ExpenseEntity(title = "Liquid Fund Emergency Reserve", category = "Contingency & Emergency Reserves", amount = 10000.00, date = "Oct 01")
        )
        masterExpenses.forEach { dao.insertExpense(it) }

        val masterSubscriptions = listOf(
            SubscriptionEntity(name = "Car Insurance Renewal", cost = 52000.00, cycle = "Annual", renewalDate = "5 days", daysLeft = 5, logoIcon = "car"),
            SubscriptionEntity(name = "Netflix 4K Premium", cost = 649.00, cycle = "Monthly", renewalDate = "Next month", daysLeft = 14, logoIcon = "netflix"),
            SubscriptionEntity(name = "Spotify Family Plan", cost = 179.00, cycle = "Monthly", renewalDate = "In 18 days", daysLeft = 18, logoIcon = "spotify"),
            SubscriptionEntity(name = "iCloud 2TB Storage", cost = 219.00, cycle = "Monthly", renewalDate = "In 22 days", daysLeft = 22, logoIcon = "icloud"),
            SubscriptionEntity(name = "ChatGPT Plus AI", cost = 1999.00, cycle = "Monthly", renewalDate = "In 28 days", daysLeft = 28, logoIcon = "chatgpt"),
            SubscriptionEntity(name = "Ring Protect Security", cost = 1200.00, cycle = "Monthly", renewalDate = "In 10 days", daysLeft = 10, logoIcon = "sub"),
            SubscriptionEntity(name = "Adobe Creative Cloud", cost = 2500.00, cycle = "Monthly", renewalDate = "In 15 days", daysLeft = 15, logoIcon = "sub"),
            SubscriptionEntity(name = "PlayStation Plus Deluxe", cost = 4999.00, cycle = "Annual", renewalDate = "In 45 days", daysLeft = 45, logoIcon = "sub"),
            SubscriptionEntity(name = "BarkBox Pet Subscription", cost = 1500.00, cycle = "Monthly", renewalDate = "In 12 days", daysLeft = 12, logoIcon = "sub")
        )
        dao.insertSubscriptions(masterSubscriptions)
    }
}
