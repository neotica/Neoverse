//
//  LoginViewModel.swift
//  Neoverse
//
//  Created by Ryo Martin on 18/01/25.
//

import Foundation
import FirebaseDatabase
import FirebaseAuth

class LoginViewModel: ObservableObject {
    @Published var version: Int? = nil
    @Published var errorMessage: String? = nil
    @Published var uid: String? = nil
    @Published var isLogin: Bool = false
    
    private let authRepo = Injection.init().provideAuthRepo()
    
    private let ref = Database.database().reference()
    
    init() {
        fetchVersion()
        fetchUid()
        isLoggedIn()
    }
    
    func fetchUid() {
        uid = authRepo.getCurrentUid()
    }
    
    func login(
        email: String,
        password: String,
        onSuccess: (() -> Void)? = nil
    ) {
        authRepo.login(email: email, password: password) { result in
            DispatchQueue.main.async {
                switch result {
                case .success:
                    print("Signed in Successfully.")
                    onSuccess?()
                case .failure(let error): print("Error signin in: \(error.localizedDescription)")
                }
            }
        }
    }
    
    func logout() {
        do {
            try authRepo.logout()
            fetchUid()
            print("Successfully signed out.")
        } catch let signOutError as NSError {
            print("Error signin out: \(signOutError)")
        }
    }
    
    func isLoggedIn() {
        if (uid == nil) {
             
            isLogin = false
        } else { isLogin = true }
    }
    
    func fetchVersion() {
        ref.child("utils/version").observeSingleEvent(of: .value) { snapshot in
            if let version = snapshot.value {
                DispatchQueue.main.async {
                    self.version = version as? Int
                    self.errorMessage = nil
                }
            } else {
                DispatchQueue.main.async {
                    self.errorMessage = "No version data found."
                }
            }
        } withCancel: { error in
            DispatchQueue.main.async {
                self.errorMessage = error.localizedDescription
            }
        }
    }
}
