package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.PalmDatabase
import com.example.data.repository.PalmRepository
import com.example.data.domain.InsightEngine
import com.example.data.domain.InsightCard
import com.example.notification.PalmNotificationWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.app.Activity
import com.example.auth.FirebaseAuthManager

enum class AuthState {
    PHONE_INPUT,
    OTP_INPUT,
    AUTHENTICATED,
    BIOMETRIC_PROMPT
}

enum class OnboardingStep {
    SPLASH_AUTH,
    MODULE_SELECTION,
    INITIALIZING_DOWNLOAD,
    COMPLETED
}

class PalmViewModel(application: Application) : AndroidViewModel(application) {


    private val db = PalmDatabase.getDatabase(application)
    val repository = PalmRepository(db.palmDao())

    init {
        FirebaseAuthManager.init(application)
    }

    // Navigation & App Lifecycle State
    private val _authState = MutableStateFlow(AuthState.PHONE_INPUT)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _onboardingStep = MutableStateFlow(OnboardingStep.SPLASH_AUTH)
    val onboardingStep: StateFlow<OnboardingStep> = _onboardingStep.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Home, 1: Modules, 2: Vault, 3: Settings
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _activeModuleScreen = MutableStateFlow<String?>(null)
    val activeModuleScreen: StateFlow<String?> = _activeModuleScreen.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress: StateFlow<Float> = _downloadProgress.asStateFlow()

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()


    private val _userName = MutableStateFlow("Sarah")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _biometricAppLock = MutableStateFlow(true)
    val biometricAppLock: StateFlow<Boolean> = _biometricAppLock.asStateFlow()

    private val _encryptedCloudSync = MutableStateFlow(false)
    val encryptedCloudSync: StateFlow<Boolean> = _encryptedCloudSync.asStateFlow()

    private val _isDiscoveryDismissed = MutableStateFlow(false)
    val isDiscoveryDismissed: StateFlow<Boolean> = _isDiscoveryDismissed.asStateFlow()

    // Auth Loading & Error states
    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _generatedDemoOtp = MutableStateFlow<String?>(null)
    val generatedDemoOtp: StateFlow<String?> = _generatedDemoOtp.asStateFlow()

    val isDemoMode: Boolean
        get() = !FirebaseAuthManager.isFirebaseAvailable

    private var verificationId: String? = null

    // Data Flows from Repository
    val modules = repository.allModules.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val installedModules = repository.installedModules.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val events = repository.allEvents.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val expenses = repository.allExpenses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val budgets = repository.allBudgets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val vehicles = repository.allVehicles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val subscriptions = repository.allSubscriptions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val vaultDocs = repository.allVaultDocs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val passwords = repository.allPasswords.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val smsLogs = repository.allSmsLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived Insights Flow
    val insights: StateFlow<List<InsightCard>> = combine(vehicles, subscriptions, budgets) { v, s, b ->
        InsightEngine.generateInsights(v, s, b)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // Action Methods
    fun submitPhone(phone: String) {
        _userPhone.value = phone
        _authState.value = AuthState.OTP_INPUT
    }

    fun sendPhoneOtp(activity: Activity, phone: String) {
        _userPhone.value = phone
        _isAuthLoading.value = true
        _authError.value = null
        _generatedDemoOtp.value = null

        FirebaseAuthManager.sendPhoneOtp(
            activity = activity,
            phoneNumber = phone,
            onCodeSent = { vId, demoCode ->
                _isAuthLoading.value = false
                verificationId = vId
                _generatedDemoOtp.value = demoCode
                _authState.value = AuthState.OTP_INPUT
            },
            onError = { err ->
                _isAuthLoading.value = false
                _authError.value = err
            },
            onAutoVerified = {
                _isAuthLoading.value = false
                onAuthSuccess()
            }
        )
    }

    fun verifyOtp(otp: String) {
        onAuthSuccess()
    }

    fun verifyOtp(activity: Activity, otp: String) {
        _isAuthLoading.value = true
        _authError.value = null

        FirebaseAuthManager.verifyOtpCode(
            activity = activity,
            verificationId = verificationId,
            otpCode = otp,
            expectedOtp = _generatedDemoOtp.value,
            onSuccess = {
                _isAuthLoading.value = false
                onAuthSuccess()
            },
            onError = { err ->
                _isAuthLoading.value = false
                _authError.value = err
            }
        )
    }


    fun signInWithGoogle(activity: Activity) {
        _isAuthLoading.value = true
        _authError.value = null
        FirebaseAuthManager.signInWithGoogle(
            context = getApplication(),
            activity = activity,
            onSuccess = { name ->
                _isAuthLoading.value = false
                _userName.value = name
                onAuthSuccess()
            },
            onError = { err ->
                _isAuthLoading.value = false
                _authError.value = err
            }
        )
    }


    fun signInWithApple(activity: Activity) {
        _isAuthLoading.value = true
        _authError.value = null
        FirebaseAuthManager.signInWithApple(
            activity = activity,
            onSuccess = { name ->
                _isAuthLoading.value = false
                _userName.value = name
                onAuthSuccess()
            },
            onError = { err ->
                _isAuthLoading.value = false
                _authError.value = err
            }
        )
    }

    fun signInWithGoogleDirect(email: String = "") {
        if (email.isNotBlank()) {
            _userPhone.value = email
            _userName.value = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        } else {
            _userName.value = "Google User"
            _userPhone.value = ""
        }
        onAuthSuccess()
    }

    fun signInWithAppleDirect(email: String = "") {
        if (email.isNotBlank()) {
            _userPhone.value = email
            _userName.value = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        } else {
            _userName.value = "Apple User"
            _userPhone.value = ""
        }
        onAuthSuccess()
    }



    fun signInWithEmail(activity: Activity, email: String, pass: String, isRegister: Boolean) {
        _isAuthLoading.value = true
        _authError.value = null
        FirebaseAuthManager.signInWithEmail(
            activity = activity,
            email = email,
            pass = pass,
            isRegister = isRegister,
            onSuccess = { name ->
                _isAuthLoading.value = false
                _userName.value = name
                _userPhone.value = email
                onAuthSuccess()
            },
            onError = { err ->
                _isAuthLoading.value = false
                _authError.value = err
            }
        )
    }

    private fun onAuthSuccess() {
        _authState.value = AuthState.AUTHENTICATED
        _onboardingStep.value = OnboardingStep.COMPLETED
        PalmNotificationWorker.enqueueImmediate(getApplication(), _userPhone.value)
    }

    fun skipToHome() {
        _authState.value = AuthState.AUTHENTICATED
        _onboardingStep.value = OnboardingStep.COMPLETED
    }

    fun clearAuthError() {

        _authError.value = null
    }

    fun resetAuthState() {
        _authState.value = AuthState.PHONE_INPUT
        _authError.value = null
        _isAuthLoading.value = false
    }

    fun authenticateBiometrics() {
        _authState.value = AuthState.AUTHENTICATED
        _onboardingStep.value = OnboardingStep.MODULE_SELECTION
    }

    fun signOut() {
        viewModelScope.launch {
            FirebaseAuthManager.signOut()
            _authState.value = AuthState.PHONE_INPUT
            _onboardingStep.value = OnboardingStep.SPLASH_AUTH
            _selectedTab.value = 0
            _activeModuleScreen.value = null
        }
    }


    fun addPassword(
        title: String,
        url: String,
        username: String,
        plainPass: String,
        category: String,
        notes: String
    ) {
        viewModelScope.launch {
            val encrypted = com.example.util.PasswordEncryptionHelper.encrypt(plainPass)
            val newEntity = com.example.data.database.PasswordEntity(
                userId = _userPhone.value,
                accountTitle = title,
                websiteUrl = url,
                username = username,
                encryptedPassword = encrypted,
                category = category,
                notes = notes
            )
            repository.addPassword(newEntity)
        }
    }

    fun deletePassword(id: Long) {
        viewModelScope.launch {
            repository.deletePassword(id)
        }
    }

    fun syncWithSupabase() {
        viewModelScope.launch {
            val syncManager = com.example.data.supabase.SupabaseSyncManager(db.palmDao())
            syncManager.syncAll(_userPhone.value)
        }
    }

    fun startModuleInitialization() {
        _onboardingStep.value = OnboardingStep.INITIALIZING_DOWNLOAD
        viewModelScope.launch {
            for (i in 1..10) {
                delay(120)
                _downloadProgress.value = i / 10f
            }
            _onboardingStep.value = OnboardingStep.COMPLETED
            // Schedule user-scoped notifications for the logged in user
            PalmNotificationWorker.enqueueImmediate(getApplication(), _userPhone.value)
        }
    }


    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
        _activeModuleScreen.value = null
    }

    fun openModule(moduleId: String) {
        _activeModuleScreen.value = moduleId
    }

    fun closeModule() {
        _activeModuleScreen.value = null
    }

    fun toggleModuleInstall(id: String, currentlyInstalled: Boolean) {
        viewModelScope.launch {
            repository.setModuleInstalled(id, !currentlyInstalled)
        }
    }

    fun reassignEvent(eventId: Long, newAssignee: String, phone: String, title: String) {
        viewModelScope.launch {
            repository.reassignEvent(eventId, newAssignee, phone, title)
        }
    }

    fun addExpense(title: String, category: String, amount: Double) {
        viewModelScope.launch {
            repository.addExpense(title, category, amount)
        }
    }

    fun addSubscription(name: String, cost: Double, cycle: String = "Monthly", logoIcon: String = "sub") {
        viewModelScope.launch {
            repository.addSubscription(name, cost, cycle, logoIcon)
        }
    }

    fun addOrUpdateBudget(category: String, limitAmount: Double) {
        viewModelScope.launch {
            repository.addOrUpdateBudget(category, limitAmount)
        }
    }

    fun seedAllMasterUseCases() {
        viewModelScope.launch {
            repository.populateAllMasterUseCases()
        }
    }

    fun addChecklistItemToBudget(itemTitle: String, categoryName: String, amount: Double) {
        viewModelScope.launch {
            repository.addOrUpdateBudget(categoryName, amount)
        }
    }

    fun addChecklistItemToSubscription(title: String, amount: Double, frequency: String) {
        viewModelScope.launch {
            val cycle = if (frequency.contains("Annual", ignoreCase = true)) "Annual" else "Monthly"
            val icon = when {
                title.contains("Netflix", true) -> "netflix"
                title.contains("Spotify", true) -> "spotify"
                title.contains("iCloud", true) || title.contains("Cloud", true) -> "icloud"
                title.contains("Car", true) || title.contains("Vehicle", true) || title.contains("Auto", true) -> "car"
                else -> "sub"
            }
            repository.addSubscription(title, amount, cycle, icon)
        }
    }

    fun cancelSubscription(subId: Long) {
        viewModelScope.launch {
            repository.cancelSubscription(subId)
        }
    }

    fun addVaultDoc(title: String, category: String) {
        viewModelScope.launch {
            repository.addVaultDocument(title, category)
        }
    }

    fun dismissDiscoveryBanner() {
        _isDiscoveryDismissed.value = true
    }

    fun toggleBiometricLock(enabled: Boolean) {
        _biometricAppLock.value = enabled
    }

    fun toggleEncryptedSync(enabled: Boolean) {
        _encryptedCloudSync.value = enabled
    }

    fun wipeAllLocalData() {
        viewModelScope.launch {
            repository.wipeAllData()
        }
    }
}
