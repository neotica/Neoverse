//
//  alexandriaApp.swift
//  alexandria
//
//  Created by Ryo Martin on 17/06/25.
//

import SwiftUI
import AlexandriaApp

@main
struct alexandriaApp: App {
    
    init {
        AlexandriaAppModulesKt.initializeKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
