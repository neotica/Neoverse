//
//  AuthRepositoryImpl.swift
//  Neoverse
//
//  Created by Ryo Martin on 18/01/25.
//

import Foundation
import FirebaseAuth
import FirebaseDatabase

class AuthRepositoryImpl: AuthRepository {
    private let auth = Auth.auth()
    private let ref = Database.database().reference()
    
    func login(email: String, password: String, completion: @escaping (Result<Void, any Error>) -> Void) {
        auth.signIn(withEmail: email, password: password) { _, error in
            if let error = error {
                completion(.failure(error))
            } else {
                completion(.success(()))
            }
        }
    }
    
    func logout() throws {
        try auth.signOut()
    }
    
    func getCurrentUid() -> String? {
        return auth.currentUser?.uid
    }
}
