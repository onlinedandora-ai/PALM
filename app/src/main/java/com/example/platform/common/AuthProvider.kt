package com.example.platform.common

/**
 * Platform-agnostic authentication contract.
 *
 * Android implementation: [com.example.platform.android.AndroidAuthProvider]
 * iOS implementation:      [com.example.platform.ios.IosAuthProvider] (Swift / Xcode project)
 *
 * Each platform implements these methods using its own SDK:
 *   • Android → Firebase Auth SDK (PhoneAuthProvider, OAuthProvider, GoogleIdToken, etc.)
 *   • iOS     → FirebaseAuth/iOS, ASAuthorizationAppleIDProvider, GIDSignIn
 */
interface AuthProvider {

    /**
     * Trigger Google sign-in on the current platform.
     * @param onSuccess   Called with the user's display name on success.
     * @param onError     Called with a human-readable error message on failure.
     */
    fun signInWithGoogle(
        onSuccess: (displayName: String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Trigger Apple sign-in on the current platform.
     * @param onSuccess   Called with the user's display name on success.
     * @param onError     Called with a human-readable error message on failure.
     */
    fun signInWithApple(
        onSuccess: (displayName: String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Send an OTP to the provided phone number.
     * On Android this triggers Firebase PhoneAuth AND posts a local notification.
     * On iOS this triggers Firebase PhoneAuth via the iOS SDK.
     *
     * @param phoneNumber   E.164-formatted number (e.g. "+919494537065").
     * @param onCodeSent    Called with (verificationId, generatedOtpCode) when SMS is dispatched.
     * @param onError       Called on failure.
     * @param onAutoVerified Called immediately if the platform auto-verifies the number.
     */
    fun sendPhoneOtp(
        phoneNumber: String,
        onCodeSent: (verificationId: String, otpCode: String) -> Unit,
        onError: (String) -> Unit,
        onAutoVerified: () -> Unit
    )

    /**
     * Verify the 6-digit OTP entered by the user.
     *
     * @param verificationId  The ID returned by [sendPhoneOtp].
     * @param otpCode         The code the user typed.
     * @param expectedOtp     The locally generated OTP (for notification-based verification).
     * @param onSuccess       Called on successful verification.
     * @param onError         Called with a message on failure.
     */
    fun verifyOtp(
        verificationId: String?,
        otpCode: String,
        expectedOtp: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Sign in with email + password (create or sign in).
     */
    fun signInWithEmail(
        email: String,
        password: String,
        isRegister: Boolean,
        onSuccess: (displayName: String) -> Unit,
        onError: (String) -> Unit
    )

    /**
     * Sign out the currently authenticated user.
     */
    fun signOut()
}
