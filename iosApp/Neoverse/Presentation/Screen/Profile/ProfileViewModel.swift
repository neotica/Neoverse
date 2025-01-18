//
//  ProfileViewModel.swift
//  Neoverse
//
//  Created by Ryo Martin on 18/01/25.
//

import Foundation

class ProfileViewModel: ObservableObject {
    @Published var version: Int? = nil
    @Published var errorMessage: String? = nil
    @Published var uid: String? = nil
    
    private let authRepo = Injection.init().provideAuthRepo()
    
    init() {
        fetchUid()
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
    
    func isLoggedIn() -> Bool {
        return if (uid == nil) {
             false
        } else { true }
    }
    
}
