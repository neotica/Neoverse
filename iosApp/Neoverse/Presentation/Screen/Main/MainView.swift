//
//  MainView.swift
//  Neoverse
//
//  Created by Ryo Martin on 18/01/25.
//

import SwiftUI
import FirebaseAuth


struct MainView: View {
    @EnvironmentObject var router: Router
    @StateObject private var viewModel = LoginViewModel()
    
    var body: some View {
        if(viewModel.isLogin) {
            MainTabView().environmentObject(router)
        } else {
            LoginView().environmentObject(router)
        }
    }
}

//                NavigationStack(path: $router.navPath){
//                    MainView().navigationDestination(for: Router.Destination.self) { destination in
//                        switch destination {
//                        case .auth: LoginView()
//                        case .home: RealtimeFirebaseView()
//                        }
//                    }
//                }
//                .environmentObject(router)



//        VStack {
//
//        }.onAppear(perform: {
//            if (viewModel.isLoggedIn()) {
//                router.navigate(to: .home)
//            } else {
//                router.navigate(to: .auth)
//            }
//        })

#Preview {
    MainView()
}
