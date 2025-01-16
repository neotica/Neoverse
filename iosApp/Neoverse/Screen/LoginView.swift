//
//  LoginView.swift
//  Neoverse
//
//  Created by Ryo Martin on 29/11/24.
//

import SwiftUI
import FirebaseAuth

struct LoginView: View {
    var body: some View {
        Text(/*@START_MENU_TOKEN@*/"Hello, World!"/*@END_MENU_TOKEN@*/)
        Button("Sign In") {
            Auth.auth().signIn(withEmail: "tarakhg@gmail.com", password: "kingkong") { result, error in
                if let error {
                    print("Error signing in: \(error.localizedDescription)")
                } else {
                    print("Signed in successfully")
                }
            }
        }
        Button("Sign Out") {
            let firebaseAuth = Auth.auth()
            do {
                try firebaseAuth.signOut()
                print("Successfully signed out.")
            } catch let signOutError as NSError {
                print("Error signin out: \(signOutError)")
            }
            
        }
    }
}

#Preview {
    LoginView()
}
