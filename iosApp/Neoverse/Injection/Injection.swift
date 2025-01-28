//
//  Injection.swift
//  Neoverse
//
//  Created by Ryo Martin on 18/01/25.
//

import Foundation

final class Injection: NSObject {
    func provideAuthRepo() -> AuthRepository {
        return AuthRepositoryImpl()
    }
}
