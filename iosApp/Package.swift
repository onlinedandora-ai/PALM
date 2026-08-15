// swift-tools-version:5.7
// Swift Package Manager dependency configuration for PALM iOS
import PackageDescription

let package = Package(
    name: "iosApp",
    platforms: [
        .iOS(.v15)
    ],
    products: [
        .library(
            name: "iosApp",
            targets: ["iosApp"])
    ],
    dependencies: [
        .package(url: "https://github.com/firebase/firebase-ios-sdk.git", from: "10.0.0"),
        .package(url: "https://github.com/google/google-signin-ios.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "iosApp",
            dependencies: [
                .product(name: "FirebaseAuth", package: "firebase-ios-sdk"),
                .product(name: "GoogleSignIn", package: "google-signin-ios")
            ],
            path: "iosApp"
        )
    ]
)
