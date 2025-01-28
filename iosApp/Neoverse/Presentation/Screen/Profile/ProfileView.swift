//
//  ProfileView.swift
//  Neoverse
//
//  Created by Ryo Martin on 18/01/25.
//

import SwiftUI

struct ProfileView: View {
    @EnvironmentObject var router: Router
    @State var isSignOutClicked = false
    
    @Environment(\.dismiss) var dismiss
    
    @StateObject private var viewModel = ProfileViewModel()
    
    var body: some View {
        
        if isSignOutClicked {
            MainView()
        } else{
            Text("Hello, World!")
            Button("Logout") {
                viewModel.logout()
                isSignOutClicked = true
                dismiss()
            }
        }
    }
}

#Preview {
    ProfileView()
}
