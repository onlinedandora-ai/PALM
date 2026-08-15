/*
 * ╔══════════════════════════════════════════════════════════════╗
 * ║           PALM — iOS Auth Provider  (Swift / Xcode)         ║
 * ║                                                              ║
 * ║  This file is a DOCUMENTATION STUB for the iOS platform.    ║
 * ║  The actual implementation lives in the iOS Xcode project    ║
 * ║  and is written in Swift, not Kotlin.                        ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * File: iosApp/iosApp/Platform/IosAuthProvider.swift
 * Conforms to: iosApp/iosApp/Platform/AuthProviderProtocol.swift
 *
 * ──────────────────────────────────────────────────────────────
 * GOOGLE SIGN-IN on iOS
 * ──────────────────────────────────────────────────────────────
 * Dependency: GoogleSignIn-iOS (CocoaPods / SPM)
 *
 * Swift pseudocode:
 *
 *   import GoogleSignIn
 *   import FirebaseAuth
 *
 *   func signInWithGoogle(onSuccess: @escaping (String) -> Void,
 *                         onError:   @escaping (String) -> Void) {
 *       guard let rootVC = UIApplication.shared.rootViewController else { return }
 *       GIDSignIn.sharedInstance.signIn(withPresenting: rootVC) { result, error in
 *           if let error = error { onError(error.localizedDescription); return }
 *           guard let user     = result?.user,
 *                 let idToken  = user.idToken?.tokenString else {
 *               onError("Missing Google ID token"); return
 *           }
 *           let credential = GoogleAuthProvider.credential(
 *               withIDToken: idToken,
 *               accessToken: user.accessToken.tokenString
 *           )
 *           Auth.auth().signIn(with: credential) { authResult, err in
 *               if let err = err { onError(err.localizedDescription); return }
 *               let name = authResult?.user.displayName ?? "Google User"
 *               onSuccess(name)
 *           }
 *       }
 *   }
 *
 * ──────────────────────────────────────────────────────────────
 * APPLE SIGN-IN on iOS  (Sign in with Apple — native)
 * ──────────────────────────────────────────────────────────────
 * Dependency: AuthenticationServices (built-in), FirebaseAuth
 *
 * Swift pseudocode:
 *
 *   import AuthenticationServices
 *   import FirebaseAuth
 *   import CryptoKit
 *
 *   func signInWithApple(onSuccess: @escaping (String) -> Void,
 *                        onError:   @escaping (String) -> Void) {
 *       let nonce    = randomNonceString()
 *       currentNonce = nonce
 *       let request  = ASAuthorizationAppleIDProvider().createRequest()
 *       request.requestedScopes = [.fullName, .email]
 *       request.nonce = sha256(nonce)
 *       let controller = ASAuthorizationController(authorizationRequests: [request])
 *       controller.delegate = self
 *       controller.presentationContextProvider = self
 *       controller.performRequests()
 *   }
 *
 *   // ASAuthorizationControllerDelegate callback:
 *   func authorizationController(controller:, didCompleteWithAuthorization auth:) {
 *       guard let appleIDCredential = auth.credential as? ASAuthorizationAppleIDCredential,
 *             let nonce             = currentNonce,
 *             let appleIDToken      = appleIDCredential.identityToken,
 *             let idTokenString     = String(data: appleIDToken, encoding: .utf8) else { return }
 *       let credential = OAuthProvider.appleCredential(
 *           withIDToken: idTokenString, rawNonce: nonce, fullName: appleIDCredential.fullName
 *       )
 *       Auth.auth().signIn(with: credential) { result, error in ... }
 *   }
 *
 * ──────────────────────────────────────────────────────────────
 * PHONE OTP on iOS
 * ──────────────────────────────────────────────────────────────
 *
 *   import FirebaseAuth
 *
 *   func sendPhoneOtp(phoneNumber: String, ...) {
 *       PhoneAuthProvider.provider().verifyPhoneNumber(phoneNumber, uiDelegate: nil) {
 *           verificationID, error in
 *           if let error = error { onError(error.localizedDescription); return }
 *           UserDefaults.standard.set(verificationID, forKey: "authVerificationID")
 *           onCodeSent(verificationID ?? "", generatedOtp)
 *       }
 *   }
 *
 *   func verifyOtp(verificationId: String, otpCode: String, ...) {
 *       let credential = PhoneAuthProvider.provider()
 *           .credential(withVerificationID: verificationId, verificationCode: otpCode)
 *       Auth.auth().signIn(with: credential) { result, error in ... }
 *   }
 *
 * ──────────────────────────────────────────────────────────────
 * iOS PROJECT SETUP (Info.plist additions required)
 * ──────────────────────────────────────────────────────────────
 *
 *   <!-- Google Sign-In URL scheme -->
 *   <key>CFBundleURLTypes</key>
 *   <array>
 *     <dict>
 *       <key>CFBundleURLSchemes</key>
 *       <array>
 *         <string>$(REVERSED_CLIENT_ID)</string>   <!-- from GoogleService-Info.plist -->
 *       </array>
 *     </dict>
 *   </array>
 *
 *   <!-- Sign in with Apple capability: enable in Xcode → Signing & Capabilities -->
 *
 * ──────────────────────────────────────────────────────────────
 * NOTE: This Kotlin file exists ONLY as cross-platform documentation.
 * The Kotlin/Compose Android code does NOT import or use this file.
 * ──────────────────────────────────────────────────────────────
 */

// Kotlin package declaration kept so the Android build ignores this file safely.
package com.example.platform.ios

// No Kotlin code — this file is documentation only.
// All iOS auth logic must be implemented in Swift in the iOS Xcode project.
