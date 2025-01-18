//
//  NeoverseApp.swift
//  Neoverse
//
//  Created by Ryo Martin on 28/11/24.
//

import SwiftUI
#if os(iOS)
import UIKit
#endif
import FirebaseCore

#if os(iOS)
class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()
        return true
    }
}
#elseif os(macOS)
class AppDelegate: NSObject, NSApplicationDelegate {
    func applicationDidFinishLaunching(_ notification: Notification) {
        FirebaseApp.configure()
    }

    func applicationShouldTerminateAfterLastWindowClosed(_ sender: NSApplication) -> Bool {
        return true // Closes the app when the last window is closed, typical behavior on macOS.
    }
}
#endif


@main
struct NeoverseApp: App {
    @ObservedObject var router = Router()
    #if os(iOS)
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    #elseif os(macOS)
    @NSApplicationDelegateAdaptor(AppDelegate.self) var delegate
    #endif

    
    var body: some Scene {
        WindowGroup {
//            ContentView()
            MainView().environmentObject(router)
            
            
        }
    }
}
