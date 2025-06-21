//
//  LoginView.swift
//  Neoverse
//
//  Created by Ryo Martin on 29/11/24.
//

import SwiftUI
import FirebaseAuth
import FirebaseDatabase
import FirebaseCore

struct RealtimeFirebaseView: View {
    @StateObject private var viewModel = LoginViewModel()
    
    var body: some View {
        
        Button("auth id") {
            print(String(viewModel.uid ?? "No id"))
        }
        Text(String(viewModel.uid ?? "No id"))
        
        Button("testversion"){
            print(String(viewModel.version ?? 0))
        }
        Text(String(viewModel.version ?? 0))
        
        Text("Hello, World!")
        Button("Sign In") {
            viewModel.login(email: "tarakhg@gmail.com", password: "kingkong")
        }
        
        Button("Sign Out") {
            viewModel.logout()
        }
    }
}

#Preview {
    LoginView()
}
