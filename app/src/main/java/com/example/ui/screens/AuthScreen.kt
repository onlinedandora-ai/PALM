package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AuthState
import com.example.ui.PalmViewModel
import com.example.ui.theme.*

// ─── Color Palette (matches dark-navy screenshot) ───────────────────────────
private val BgDeep        = Color(0xFF091629)
private val BgMid         = Color(0xFF0F253F)
private val BgLight       = Color(0xFF0B192C)
private val CardBg        = Color(0xFF13233A)
private val AccentCyan    = Color(0xFF00B4D8)
private val AccentCyanDim = Color(0xFF48CAE4)
private val GoogleBlue    = Color(0xFF4285F4)
private val AppleBlack    = Color(0xFF0A0A0A)
private val BorderCyan    = Color(0xFF1A4A6E)
private val TextSub       = Color(0xFF90E0EF)
private val TextMuted     = Color(0xFF5A7A9A)
private val AmberWarn     = Color(0xFFFFB703)

// ─── Gradient ────────────────────────────────────────────────────────────────
private val DarkNavyGradient = Brush.verticalGradient(
    colors = listOf(BgDeep, BgMid, BgLight)
)

@Composable
fun AuthScreen(viewModel: PalmViewModel) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    val authState     by viewModel.authState.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authError     by viewModel.authError.collectAsState()
    val realOtpCode   by viewModel.generatedDemoOtp.collectAsState()

    // Country selector data
    val countryList = remember {
        listOf(
            CountryCode("India",         "🇮🇳", "+91",  "Mobile Number"),
            CountryCode("United States", "🇺🇸", "+1",   "Mobile Number"),
            CountryCode("United Kingdom","🇬🇧", "+44",  "Mobile Number"),
            CountryCode("UAE",           "🇦🇪", "+971", "Mobile Number"),
            CountryCode("Canada",        "🇨🇦", "+1",   "Mobile Number"),
            CountryCode("Australia",     "🇦🇺", "+61",  "Mobile Number"),
            CountryCode("Singapore",     "🇸🇬", "+65",  "Mobile Number"),
        )
    }

    var selectedCountry      by remember { mutableStateOf(countryList[0]) }
    var showCountryDropdown  by remember { mutableStateOf(false) }
    var phoneInput           by remember { mutableStateOf("") }
    var otpInput             by remember { mutableStateOf("") }

    // Active sub-form: null = main screen, "phone", "email"
    var activeFormMode by remember { mutableStateOf<String?>(null) }

    // Email form
    var emailInput       by remember { mutableStateOf("") }
    var passwordInput    by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isRegisterMode   by remember { mutableStateOf(false) }

    // Auto-fill OTP from notification
    LaunchedEffect(realOtpCode) {
        realOtpCode?.let { otpInput = it }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyGradient)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // ── PALM Logo ────────────────────────────────────────────────────
            PalmLogoEmblem()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Welcome to PALM",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your Personal Assistant &\nLifestyle Manager",
                fontSize = 14.sp,
                color = TextSub,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ── Error Banner ─────────────────────────────────────────────────
            authError?.let { err ->
                AuthErrorBanner(message = err, onDismiss = { viewModel.clearAuthError() })
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Main Content (animated switching) ────────────────────────────
            AnimatedContent(
                targetState = when {
                    authState == AuthState.OTP_INPUT -> "otp"
                    activeFormMode != null           -> activeFormMode!!
                    else                             -> "main"
                },
                transitionSpec = {
                    (fadeIn() + slideInHorizontally { it / 3 })
                        .togetherWith(fadeOut() + slideOutHorizontally { -it / 3 })
                },
                label = "auth_content"
            ) { screen ->
                when (screen) {
                    // ── OTP VERIFICATION ─────────────────────────────────────
                    "otp" -> OtpInputCard(
                        otpInput         = otpInput,
                        isAuthLoading    = isAuthLoading,
                        phoneDisplay     = "${selectedCountry.flag} ${selectedCountry.code} $phoneInput",
                        onOtpChange      = { if (it.length <= 6) otpInput = it },
                        onVerify         = { viewModel.verifyOtp(otpInput) },
                        onBack           = {
                            viewModel.resetAuthState()
                            activeFormMode = null
                        }
                    )

                    // ── PHONE INPUT ───────────────────────────────────────────
                    "phone" -> PhoneInputCard(
                        selectedCountry     = selectedCountry,
                        countryList         = countryList,
                        showCountryDropdown = showCountryDropdown,
                        phoneInput          = phoneInput,
                        isAuthLoading       = isAuthLoading,
                        onCountrySelect     = { selectedCountry = it; showCountryDropdown = false },
                        onDropdownToggle    = { showCountryDropdown = it },
                        onPhoneChange       = { phoneInput = it },
                        onSendOtp           = {
                            val fullPhone = "${selectedCountry.code}$phoneInput"
                            if (activity != null) viewModel.sendPhoneOtp(activity, fullPhone)
                            else viewModel.submitPhone(fullPhone)
                        },
                        onBack              = { activeFormMode = null }
                    )

                    // ── EMAIL INPUT ───────────────────────────────────────────
                    "email" -> EmailInputCard(
                        emailInput       = emailInput,
                        passwordInput    = passwordInput,
                        isPasswordVisible= isPasswordVisible,
                        isRegisterMode   = isRegisterMode,
                        isAuthLoading    = isAuthLoading,
                        onEmailChange    = { emailInput = it },
                        onPasswordChange = { passwordInput = it },
                        onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                        onToggleMode     = { isRegisterMode = !isRegisterMode },
                        onSubmit         = {
                            if (activity != null)
                                viewModel.signInWithEmail(activity, emailInput, passwordInput, isRegisterMode)
                            else
                                viewModel.submitPhone(emailInput)
                        },
                        onBack           = { activeFormMode = null }
                    )

                    // ── MAIN WELCOME SCREEN ───────────────────────────────────
                    else -> MainAuthButtons(
                        isAuthLoading     = isAuthLoading,
                        onPhoneClick      = { activeFormMode = "phone" },
                        onEmailClick      = { activeFormMode = "email" },
                        onGoogleClick     = {
                            // ✅ FIX: Directly trigger Google sign-in — no intermediate form
                            if (activity != null) viewModel.signInWithGoogle(activity)
                            else viewModel.signInWithGoogleDirect()
                        },
                        onAppleClick      = {
                            // ✅ FIX: Directly trigger Apple sign-in — no intermediate form
                            if (activity != null) viewModel.signInWithApple(activity)
                            else viewModel.signInWithAppleDirect()
                        },
                        onSignUpClick     = { activeFormMode = "email"; isRegisterMode = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PALM Logo Emblem
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PalmLogoEmblem() {
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        AccentCyan.copy(alpha = 0.25f),
                        AccentCyan.copy(alpha = 0.05f)
                    )
                )
            )
            .border(2.dp, AccentCyan.copy(alpha = 0.6f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Inner circle
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(AccentCyan.copy(alpha = 0.12f))
                .border(1.dp, AccentCyan.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = "PALM Logo",
                tint = AccentCyan,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Error Banner
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AuthErrorBanner(message: String, onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AmberWarn.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarn.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = AmberWarn, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = message, fontSize = 12.sp, color = Color.White, modifier = Modifier.weight(1f))
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Auth Buttons — matches reference screenshot exactly
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MainAuthButtons(
    isAuthLoading: Boolean,
    onPhoneClick: () -> Unit,
    onEmailClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onAppleClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Login with Phone — outlined pill
        OutlinedButton(
            onClick = onPhoneClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_login_phone"),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(
                Icons.Default.PhoneIphone,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Login with Phone", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        // 2. Login with Email — outlined pill
        OutlinedButton(
            onClick = onEmailClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_login_email"),
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(
                Icons.Default.MailOutline,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Login with Email", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        // OR Divider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.18f))
            Text(
                "   OR   ",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.45f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.18f))
        }

        // 3. Continue with Google — filled blue pill ✅ FIXED: direct sign-in
        Button(
            onClick = onGoogleClick,
            enabled = !isAuthLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_google_signin"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GoogleBlue,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            if (isAuthLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Continue with Google",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 4. Continue with Apple — filled black pill ✅ FIXED: direct sign-in
        Button(
            onClick = onAppleClick,
            enabled = !isAuthLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_apple_signin"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppleBlack,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
        ) {
            if (isAuthLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Continue with Apple",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Footer links
        Spacer(modifier = Modifier.height(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Text(
                    "Don't have an account? ",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "Sign Up",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Terms and Cookies  Privacy Policy",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.35f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Phone Input Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PhoneInputCard(
    selectedCountry: CountryCode,
    countryList: List<CountryCode>,
    showCountryDropdown: Boolean,
    phoneInput: String,
    isAuthLoading: Boolean,
    onCountrySelect: (CountryCode) -> Unit,
    onDropdownToggle: (Boolean) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSendOtp: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    "Login with Phone Number",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Country + Phone row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgLight)
                    .border(1.dp, BorderCyan, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Row(
                        modifier = Modifier.clickable { onDropdownToggle(true) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${selectedCountry.flag} ${selectedCountry.code}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSub)
                    }

                    DropdownMenu(
                        expanded = showCountryDropdown,
                        onDismissRequest = { onDropdownToggle(false) }
                    ) {
                        countryList.forEach { country ->
                            DropdownMenuItem(
                                text = { Text("${country.flag} ${country.name} (${country.code})") },
                                onClick = { onCountrySelect(country) }
                            )
                        }
                    }
                }

                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .height(24.dp),
                    color = BorderCyan
                )

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = onPhoneChange,
                    placeholder = { Text("Mobile number", color = TextMuted, fontSize = 14.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor     = Color.White,
                        unfocusedTextColor   = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onSendOtp,
                enabled = !isAuthLoading && phoneInput.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor   = Color(0xFF0B192C)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isAuthLoading) {
                    CircularProgressIndicator(color = BgDeep, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Send OTP Code", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// OTP Input Card — matches 3rd screenshot (OTP screen)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OtpInputCard(
    otpInput: String,
    isAuthLoading: Boolean,
    phoneDisplay: String,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    "Enter OTP Code",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sent to $phoneDisplay",
                fontSize = 12.sp,
                color = TextSub,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = otpInput,
                onValueChange = onOtpChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    textAlign  = TextAlign.Center,
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                    letterSpacing = 10.sp
                ),
                placeholder = {
                    Text(
                        "000000",
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier.fillMaxWidth(),
                        color      = TextMuted,
                        fontSize   = 22.sp,
                        letterSpacing = 8.sp
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AccentCyan,
                    unfocusedBorderColor = BorderCyan,
                    focusedContainerColor   = BgLight,
                    unfocusedContainerColor = BgLight
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onVerify,
                enabled = !isAuthLoading && otpInput.length == 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor   = Color(0xFF0B192C)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isAuthLoading) {
                    CircularProgressIndicator(color = BgDeep, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        "Verify Code & Access Vault",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Email Input Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EmailInputCard(
    emailInput: String,
    passwordInput: String,
    isPasswordVisible: Boolean,
    isRegisterMode: Boolean,
    isAuthLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleMode: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        if (isRegisterMode) "Create Account" else "Login with Email",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                TextButton(onClick = onToggleMode) {
                    Text(
                        if (isRegisterMode) "Sign In" else "Register",
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = emailInput,
                onValueChange = onEmailChange,
                placeholder = { Text("Email address", color = TextMuted) },
                leadingIcon  = { Icon(Icons.Default.Email, contentDescription = null, tint = AccentCyan) },
                singleLine   = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier     = Modifier.fillMaxWidth(),
                shape        = RoundedCornerShape(12.dp),
                colors       = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = BgLight,
                    unfocusedContainerColor = BgLight,
                    focusedBorderColor      = AccentCyan,
                    unfocusedBorderColor    = BorderCyan,
                    focusedTextColor        = Color.White,
                    unfocusedTextColor      = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = passwordInput,
                onValueChange = onPasswordChange,
                placeholder   = { Text("Password (min 6 chars)", color = TextMuted) },
                leadingIcon   = { Icon(Icons.Default.Lock, contentDescription = null, tint = AccentCyan) },
                trailingIcon  = {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }
                },
                singleLine   = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier     = Modifier.fillMaxWidth(),
                shape        = RoundedCornerShape(12.dp),
                colors       = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = BgLight,
                    unfocusedContainerColor = BgLight,
                    focusedBorderColor      = AccentCyan,
                    unfocusedBorderColor    = BorderCyan,
                    focusedTextColor        = Color.White,
                    unfocusedTextColor      = Color.White
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick  = onSubmit,
                enabled  = !isAuthLoading && emailInput.isNotBlank() && passwordInput.length >= 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = AccentCyan,
                    contentColor   = Color(0xFF0B192C)
                ),
                shape    = RoundedCornerShape(12.dp)
            ) {
                if (isAuthLoading) {
                    CircularProgressIndicator(color = BgDeep, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        if (isRegisterMode) "Register Account" else "Sign In with Email",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Data class + extension
// ─────────────────────────────────────────────────────────────────────────────
data class CountryCode(
    val name: String,
    val flag: String,
    val code: String,
    val example: String
)

fun android.content.Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is android.content.ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
