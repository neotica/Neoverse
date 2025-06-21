//
//  alexandriaApp.swift
//  alexandria
//
//  Created by Ryo Martin on 17/06/25.
//

import SwiftUI
import AlexandriaApp
import Firebase
import FirebaseCore
import FirebaseFirestore
import FirebaseAuth

@main
struct alexandriaApp: App {
    
    init() {
        FirebaseApp.configure()
        AlexandriaAppModulesKt.initializeKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
