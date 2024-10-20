package id.neotica.neoversewear.di

import id.neotica.database.CurrentUserRepositoryImpl
import id.neotica.database.FriendDaoRepositoryImpl
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.AuthRepository
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.neoversewear.presentation.screen.auth.WLoginViewModel
import id.neotica.neoversewear.presentation.screen.chat.WChatViewModel
import id.neotica.neoversewear.presentation.screen.home.WHomeViewModel
import id.neotica.neoversewear.presentation.screen.profile.WProfileViewModel
import id.neotica.neoversewear.presentation.screen.world.WBarViewModel
import id.neotica.network.AuraRepositoryImpl
import id.neotica.network.AuthRepositoryImpl
import id.neotica.network.ChatRepositoryImpl
import id.neotica.network.UserRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val wearModules = module {
    viewModelOf(::WLoginViewModel)
    viewModelOf(::WHomeViewModel)
    viewModelOf(::WProfileViewModel)
    viewModelOf(::WChatViewModel)
    viewModelOf(::WBarViewModel)

    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::UserRepositoryImpl) bind UserRepository::class
    singleOf(::ChatRepositoryImpl) bind ChatRepository::class
    singleOf(::CurrentUserRepositoryImpl) bind CurrentUserRepository::class
    singleOf(::FriendDaoRepositoryImpl) bind FriendDaoRepository::class
    singleOf(::AuraRepositoryImpl) bind AuraRepository::class

    singleOf(::GetUserIdUseCase)
}