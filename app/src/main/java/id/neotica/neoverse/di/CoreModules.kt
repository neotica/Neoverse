package id.neotica.neoverse.di

import id.neotica.database.CurrentUserRepositoryImpl
import id.neotica.database.FriendDaoRepositoryImpl
import id.neotica.network.AuthRepositoryImpl
import id.neotica.network.ChatRepositoryImpl
import id.neotica.network.UserRepositoryImpl
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.remote.AuthRepository
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.neoverse.presentation.screen.auth.login.LoginViewModel
import id.neotica.neoverse.presentation.screen.auth.register.RegisterViewModel
import id.neotica.neoverse.presentation.screen.auth.verify.VerifyEmailViewModel
import id.neotica.neoverse.presentation.screen.chat.ChatViewModel
import id.neotica.neoverse.presentation.screen.friend.FriendViewModel
import id.neotica.neoverse.presentation.screen.dump.DumpViewModel
import id.neotica.neoverse.presentation.screen.home.HomeViewModel
import id.neotica.neoverse.presentation.screen.profile.ProfileViewModel
import id.neotica.network.AuraRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModules = module {
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>()}
    singleOf(::UserRepositoryImpl) { bind<UserRepository>()}
    singleOf(::ChatRepositoryImpl) bind ChatRepository::class
    singleOf(::AuraRepositoryImpl) bind AuraRepository::class
    singleOf(::FriendDaoRepositoryImpl) bind FriendDaoRepository::class
    singleOf(::CurrentUserRepositoryImpl) bind CurrentUserRepository::class
}

val viewModelModules = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::FriendViewModel)
    viewModelOf(::VerifyEmailViewModel)
    viewModelOf(::DumpViewModel)
}

val useCaseModules = module {
    singleOf(::GetUserIdUseCase)
}

val domainModules = module {
//    factoryOf(::GetUserIdUseCase)
}