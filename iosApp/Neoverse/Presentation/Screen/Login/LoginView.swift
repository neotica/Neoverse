//
//  LoginView.swift
//  Neoverse
//
//  Created by Ryo Martin on 29/11/24.
//

import SwiftUI
import FirebaseAuth

struct LoginView: View {
    @EnvironmentObject var router: Router
    @State var isSignInClicked = false
    
    @StateObject private var viewModel = LoginViewModel()
    
    var body: some View {
        if isSignInClicked {
            MainView().environmentObject(router)
        } else {
            Text("Hello, World!")
            Button("Sign In") {
            }
            Button("Sign Out") {
                viewModel.logout()
            }
        }
    }
}

#Preview {
    LoginView()
}
