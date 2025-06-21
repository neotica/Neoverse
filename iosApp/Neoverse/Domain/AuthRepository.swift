//
//  AuthRepository.swift
//  Neoverse
//
//  Created by Ryo Martin on 18/01/25.
//

import Foundation

protocol AuthRepository {
    func login(email: String, password: String, completion: @escaping (Result<Void, Error>) -> Void)
    func logout() throws
    func getCurrentUid() -> String?
}
