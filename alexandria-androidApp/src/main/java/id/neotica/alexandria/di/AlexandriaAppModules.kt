package id.neotica.alexandria.di

import id.neotica.auth.di.authModules
import id.neotica.database.CurrentUserRepositoryImpl
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.remote.AlexandriaAdminRepository
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.domain.remote.AuthRepository
import id.neotica.domain.remote.FirebaseRepository
import id.neotica.domain.remote.PermissionRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.network.AlexandriaAdminRepositoryImpl
import id.neotica.network.AlexandriaRepositoryImpl
import id.neotica.network.AuthRepositoryImpl
import id.neotica.network.FirebaseRepositoryImpl
import id.neotica.network.PermissionRepositoryImpl
import id.neotica.network.UserRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModules = module {
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::CurrentUserRepositoryImpl) bind CurrentUserRepository::class
    singleOf(::FirebaseRepositoryImpl) bind FirebaseRepository::class
    singleOf(::AlexandriaRepositoryImpl) bind AlexandriaRepository::class
    singleOf(::AlexandriaAdminRepositoryImpl) bind AlexandriaAdminRepository::class
    singleOf(::PermissionRepositoryImpl) bind PermissionRepository::class
}

val viewModelModules = module {
    includes(authModules)
    includes(alexandriaModules)
}

val useCaseModules = module {
    singleOf(::GetUserIdUseCase)
    singleOf(::GetUserNameUseCase)
}