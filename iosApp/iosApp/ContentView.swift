import SwiftUI

struct ContentView: View {
    @State private var isAuthenticated = false
    @State private var userName = ""
    @State private var selectedTab = 0
    @State private var emailInput = ""
    @State private var passwordInput = ""
    @State private var phoneInput = ""
    @State private var otpInput = ""
    @State private var activeVerificationId: String?
    @State private var expectedOtpCode: String?
    @State private var statusMessage = ""
    @State private var isShowingOtpDialog = false

    private let authProvider: AuthProviderProtocol = IosAuthProvider.shared

    var body: some View {
        NavigationView {
            if isAuthenticated {
                mainDashboardView
            } else {
                authenticationView
            }
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }

    // MARK: - Main App Dashboard
    private var mainDashboardView: View {
        VStack(spacing: 0) {
            // Header Bar
            HStack {
                VStack(alignment: .leading) {
                    Text("🌴 PALM Lifestyle")
                        .font(.title2)
                        .bold()
                        .foregroundColor(.primary)
                    Text("Welcome back, \(userName)!")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                Spacer()
                Button(action: handleSignOut) {
                    Image(systemName: "rectangle.portrait.and.arrow.right")
                        .foregroundColor(.red)
                        .padding(8)
                        .background(Color.red.opacity(0.1))
                        .clipShape(Circle())
                }
            }
            .padding()
            .background(Color(UIColor.systemBackground))
            .shadow(color: Color.black.opacity(0.05), radius: 5, x: 0, y: 2)

            TabView(selection: $selectedTab) {
                // Tab 1: Home Modules
                ScrollView {
                    VStack(spacing: 16) {
                        moduleCard(
                            icon: "lock.shield.fill",
                            title: "Password Manager",
                            subtitle: "AES-256 Vault & Keyring",
                            color: .indigo
                        )
                        moduleCard(
                            icon: "banknote.fill",
                            title: "Household Finance",
                            subtitle: "Budgeting & Bill Reminders",
                            color: .emeraldColor
                        )
                        moduleCard(
                            icon: "car.fill",
                            title: "Vehicle Hub",
                            subtitle: "Service, Fuel & Insurance Tracker",
                            color: .orange
                        )
                        moduleCard(
                            icon: "repeat.circle.fill",
                            title: "Subscriptions",
                            subtitle: "Recurring Payments & Audit",
                            color: .purple
                        )
                    }
                    .padding()
                }
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("Dashboard")
                }
                .tag(0)

                // Tab 2: Settings & Security
                VStack(spacing: 20) {
                    Text("Security & Preferences")
                        .font(.headline)
                        .padding(.top)

                    List {
                        Section(header: Text("Authentication Status")) {
                            HStack {
                                Text("Signed in as")
                                Spacer()
                                Text(userName).foregroundColor(.blue)
                            }
                            HStack {
                                Text("Security Engine")
                                Spacer()
                                Text("iOS Native + Firebase").foregroundColor(.green)
                            }
                        }

                        Section(header: Text("Platform Settings")) {
                            Toggle("Biometric Authentication", isOn: .constant(true))
                            Toggle("Push Notifications", isOn: .constant(true))
                        }
                    }
                }
                .tabItem {
                    Image(systemName: "gearshape.fill")
                    Text("Settings")
                }
                .tag(1)
            }
        }
    }

    // MARK: - Authentication View
    private var authenticationView: View {
        ScrollView {
            VStack(spacing: 24) {
                VStack(spacing: 8) {
                    Image(systemName: "palm.tree.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 64, height: 64)
                        .foregroundColor(.green)

                    Text("PALM Mobile")
                        .font(.largeTitle)
                        .bold()

                    Text("Personal Life & Account Manager")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding(.top, 40)

                if !statusMessage.isEmpty {
                    Text(statusMessage)
                        .font(.caption)
                        .foregroundColor(.blue)
                        .padding(8)
                        .background(Color.blue.opacity(0.1))
                        .cornerRadius(8)
                }

                // Social Sign-In Section
                VStack(spacing: 12) {
                    Button(action: handleGoogleSignIn) {
                        HStack {
                            Image(systemName: "g.circle.fill")
                                .font(.title3)
                            Text("Sign in with Google")
                                .fontWeight(.semibold)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color(UIColor.secondarySystemBackground))
                        .foregroundColor(.primary)
                        .cornerRadius(12)
                    }

                    Button(action: handleAppleSignIn) {
                        HStack {
                            Image(systemName: "applelogo")
                                .font(.title3)
                            Text("Sign in with Apple")
                                .fontWeight(.semibold)
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.primary)
                        .foregroundColor(Color(UIColor.systemBackground))
                        .cornerRadius(12)
                    }
                }

                Divider().padding(.vertical, 8)

                // Phone Verification Section
                VStack(alignment: .leading, spacing: 8) {
                    Text("Phone Verification")
                        .font(.headline)

                    HStack {
                        TextField("Phone (+91 94945 37065)", text: $phoneInput)
                            .keyboardType(.phonePad)
                            .padding()
                            .background(Color(UIColor.secondarySystemBackground))
                            .cornerRadius(10)

                        Button("Send OTP") {
                            handleSendOtp()
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 14)
                        .background(Color.green)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                    }

                    if isShowingOtpDialog {
                        VStack(alignment: .leading, spacing: 8) {
                            TextField("Enter 6-digit OTP", text: $otpInput)
                                .keyboardType(.numberPad)
                                .padding()
                                .background(Color(UIColor.secondarySystemBackground))
                                .cornerRadius(10)

                            Button(action: handleVerifyOtp) {
                                Text("Verify & Sign In")
                                    .fontWeight(.bold)
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(Color.blue)
                                    .foregroundColor(.white)
                                    .cornerRadius(10)
                            }
                        }
                        .padding(.top, 8)
                    }
                }

                Divider().padding(.vertical, 8)

                // Email / Password Section
                VStack(alignment: .leading, spacing: 12) {
                    Text("Email Account")
                        .font(.headline)

                    TextField("Email Address", text: $emailInput)
                        .keyboardType(.emailAddress)
                        .autocapitalization(.none)
                        .padding()
                        .background(Color(UIColor.secondarySystemBackground))
                        .cornerRadius(10)

                    SecureField("Password", text: $passwordInput)
                        .padding()
                        .background(Color(UIColor.secondarySystemBackground))
                        .cornerRadius(10)

                    HStack(spacing: 12) {
                        Button(action: { handleEmailAuth(isRegister: false) }) {
                            Text("Sign In")
                                .fontWeight(.semibold)
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                        }

                        Button(action: { handleEmailAuth(isRegister: true) }) {
                            Text("Register")
                                .fontWeight(.semibold)
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.gray.opacity(0.2))
                                .foregroundColor(.primary)
                                .cornerRadius(10)
                        }
                    }
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 40)
        }
    }

    // MARK: - Component Helpers
    private func moduleCard(icon: String, title: String, subtitle: String, color: Color) -> some View {
        HStack(spacing: 16) {
            Image(systemName: icon)
                .font(.title2)
                .foregroundColor(.white)
                .frame(width: 48, height: 48)
                .background(color)
                .cornerRadius(12)

            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.headline)
                Text(subtitle)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            Spacer()
            Image(systemName: "chevron.right")
                .foregroundColor(.gray)
        }
        .padding()
        .background(Color(UIColor.secondarySystemBackground))
        .cornerRadius(14)
    }

    // MARK: - Action Handlers
    private func handleGoogleSignIn() {
        statusMessage = "Initiating Google Sign-In..."
        authProvider.signInWithGoogle(
            onSuccess: { name in
                self.userName = name
                self.isAuthenticated = true
                self.statusMessage = ""
            },
            onError: { err in
                self.statusMessage = "Google error: \(err)"
            }
        )
    }

    private func handleAppleSignIn() {
        statusMessage = "Initiating Apple Sign-In..."
        authProvider.signInWithApple(
            onSuccess: { name in
                self.userName = name
                self.isAuthenticated = true
                self.statusMessage = ""
            },
            onError: { err in
                self.statusMessage = "Apple error: \(err)"
            }
        )
    }

    private func handleSendOtp() {
        guard !phoneInput.isEmpty else {
            statusMessage = "Please enter a phone number."
            return
        }
        statusMessage = "Sending OTP code..."
        authProvider.sendPhoneOtp(
            phoneNumber: phoneInput,
            onCodeSent: { vId, otp in
                self.activeVerificationId = vId
                self.expectedOtpCode = otp
                self.isShowingOtpDialog = true
                self.statusMessage = "OTP sent! Test code is \(otp)"
            },
            onError: { err in
                self.statusMessage = "OTP Send error: \(err)"
            },
            onAutoVerified: {
                self.userName = "Phone User (\(phoneInput))"
                self.isAuthenticated = true
            }
        )
    }

    private func handleVerifyOtp() {
        authProvider.verifyOtp(
            verificationId: activeVerificationId,
            otpCode: otpInput,
            expectedOtp: expectedOtpCode,
            onSuccess: {
                self.userName = "Phone User (\(phoneInput))"
                self.isAuthenticated = true
                self.statusMessage = ""
            },
            onError: { err in
                self.statusMessage = err
            }
        )
    }

    private func handleEmailAuth(isRegister: Bool) {
        statusMessage = isRegister ? "Registering user..." : "Signing in..."
        authProvider.signInWithEmail(
            email: emailInput,
            password: passwordInput,
            isRegister: isRegister,
            onSuccess: { name in
                self.userName = name
                self.isAuthenticated = true
                self.statusMessage = ""
            },
            onError: { err in
                self.statusMessage = err
            }
        )
    }

    private func handleSignOut() {
        authProvider.signOut()
        isAuthenticated = false
        userName = ""
        statusMessage = "Signed out successfully."
    }
}

extension Color {
    static let emeraldColor = Color(red: 16/255, green: 185/255, blue: 129/255)
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
