import SwiftUI

#if canImport(FirebaseCore)
import FirebaseCore
#endif

@main
struct PALMApp: App {

    init() {
        #if canImport(FirebaseCore)
        // Initialize Firebase SDK if GoogleService-Info.plist is present
        if Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil {
            FirebaseApp.configure()
            print("[PALM iOS] Firebase configured successfully.")
        } else {
            print("[PALM iOS] GoogleService-Info.plist not found. Operating in standalone demo mode.")
        }
        #else
        print("[PALM iOS] Running in standalone SwiftUI mode.")
        #endif
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
