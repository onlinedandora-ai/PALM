# PALM Lifestyle — Native iOS Application

This directory contains the complete native **iOS project structure** (`iosApp`) for the PALM app, built with **Swift** and **SwiftUI**.

## 📁 Project Layout

```
iosApp/
├── iosApp.xcodeproj/               # Xcode project file
│   └── project.pbxproj             # Xcode build configuration
├── iosApp/                         # Swift source root
│   ├── PALMApp.swift               # SwiftUI @main entry point & Firebase configuration
│   ├── ContentView.swift           # Native SwiftUI screens & module dashboard
│   ├── Info.plist                  # Bundle config, OAuth schemes & permissions
│   └── Platform/
│       ├── AuthProviderProtocol.swift # Swift protocol matching AuthProvider.kt
│       └── IosAuthProvider.swift      # Native Swift Auth implementation
├── Podfile                         # CocoaPods dependency declaration
├── Package.swift                   # Swift Package Manager manifest
└── README.md                       # Documentation
```

## 🚀 Getting Started on macOS

### 1. Opening in Xcode
Double click `iosApp.xcodeproj` or run in terminal:
```bash
open iosApp.xcodeproj
```

### 2. Installing Dependencies
If using CocoaPods:
```bash
cd iosApp
pod install
open iosApp.xcworkspace
```
Or use Xcode's built-in Swift Package Manager (`File -> Add Package Dependencies...`).

### 3. Firebase Configuration
To connect live Firebase Auth on iOS:
1. Go to your [Firebase Console](https://console.firebase.google.com/).
2. Add an iOS app with bundle ID `com.palm.lifestyle.ios`.
3. Download `GoogleService-Info.plist` and drag it into `iosApp/iosApp/` inside Xcode.
4. Update the `REVERSED_CLIENT_ID` in `Info.plist` with the reversed client ID from `GoogleService-Info.plist`.

### 4. Running the App
Select an iOS Simulator (e.g. iPhone 15 Pro) or a connected physical iOS device in Xcode, then click **Run** (`⌘R`).
