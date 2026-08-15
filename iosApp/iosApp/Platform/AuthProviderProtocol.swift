import Foundation

/// Platform-agnostic authentication protocol for iOS.
/// Matches the Kotlin `AuthProvider.kt` contract on Android.
public protocol AuthProviderProtocol {

    /// Trigger Google sign-in on iOS using `GoogleSignIn-iOS` and `FirebaseAuth`.
    /// - Parameters:
    ///   - onSuccess: Called with the user's display name upon successful sign-in.
    ///   - onError: Called with a human-readable error description on failure.
    func signInWithGoogle(
        onSuccess: @escaping (_ displayName: String) -> Void,
        onError: @escaping (_ errorMessage: String) -> Void
    )

    /// Trigger native Apple sign-in on iOS using `AuthenticationServices` and `FirebaseAuth`.
    /// - Parameters:
    ///   - onSuccess: Called with the user's display name upon successful sign-in.
    ///   - onError: Called with a human-readable error description on failure.
    func signInWithApple(
        onSuccess: @escaping (_ displayName: String) -> Void,
        onError: @escaping (_ errorMessage: String) -> Void
    )

    /// Send an OTP to the provided E.164 phone number via Firebase PhoneAuthProvider.
    /// - Parameters:
    ///   - phoneNumber: E.164-formatted phone number string (e.g., "+919494537065").
    ///   - onCodeSent: Called with (verificationId, generatedOtpCode).
    ///   - onError: Called on failure.
    ///   - onAutoVerified: Called if the iOS platform auto-verifies the phone credential.
    func sendPhoneOtp(
        phoneNumber: String,
        onCodeSent: @escaping (_ verificationId: String, _ otpCode: String) -> Void,
        onError: @escaping (_ errorMessage: String) -> Void,
        onAutoVerified: @escaping () -> Void
    )

    /// Verify the 6-digit OTP code entered by the user.
    /// - Parameters:
    ///   - verificationId: Verification ID returned by `sendPhoneOtp`.
    ///   - otpCode: Code entered by the user.
    ///   - expectedOtp: Expected local test code (if applicable).
    ///   - onSuccess: Called on successful verification.
    ///   - onError: Called with error message on failure.
    func verifyOtp(
        verificationId: String?,
        otpCode: String,
        expectedOtp: String?,
        onSuccess: @escaping () -> Void,
        onError: @escaping (_ errorMessage: String) -> Void
    )

    /// Sign in or register with email and password using Firebase Auth.
    /// - Parameters:
    ///   - email: User email address.
    ///   - password: Password (minimum 6 characters).
    ///   - isRegister: True to create a new user; false to sign in.
    ///   - onSuccess: Called with user display name.
    ///   - onError: Called with error message.
    func signInWithEmail(
        email: String,
        password: String,
        isRegister: Bool,
        onSuccess: @escaping (_ displayName: String) -> Void,
        onError: @escaping (_ errorMessage: String) -> Void
    )

    /// Sign out the currently authenticated user.
    func signOut()
}
