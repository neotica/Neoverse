//
//  MainTabView.swift
//  Neoverse
//
//  Created by Ryo Martin on 18/01/25.
//

import SwiftUI

struct MainTabView: View {
    @EnvironmentObject var router: Router
    @StateObject private var viewModel = LoginViewModel()
    
    var body: some View {
        TabView {
            Tab(
                "Home",
                systemImage: "house"
            ) {
                LoginView()
            }
            Tab(
                "World",
                systemImage: "globe"
            ) {
                RealtimeFirebaseView()
            }
            Tab(
                "Profile",
                systemImage: "person"
            ) {
                ProfileView()
            }

        }.environmentObject(router)
    }
}

#Preview {
    MainTabView()
}
