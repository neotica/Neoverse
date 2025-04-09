package id.neotica.neoverse.di

import id.neotica.auth.di.authModules
import id.neotica.database.CurrentUserRepositoryImpl
import id.neotica.database.FriendDaoRepositoryImpl
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.observer.ConnectivityObserver
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.AuthRepository
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.remote.FeedRepository
import id.neotica.domain.remote.FirebaseRepository
import id.neotica.domain.remote.InventoryRepository
import id.neotica.domain.remote.MarketRepository
import id.neotica.domain.remote.MerchantRepository
import id.neotica.domain.remote.RavenRepository
import id.neotica.domain.remote.SuperAdminRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.usecase.BuyHeroUseCase
import id.neotica.domain.usecase.BuyItemUseCase
import id.neotica.domain.usecase.GetAuraUseCase
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.neoverse.presentation.screen.camerax.CameraPreviewViewModel
import id.neotica.chat.ChatViewModel
import id.neotica.domain.remote.AlexandriaRepository
import id.neotica.domain.remote.PermissionRepository
import id.neotica.neoverse.presentation.screen.dump.DumpViewModel
import id.neotica.friend.FriendViewModel
import id.neotica.home.HomeViewModel
import id.neotica.home.addchat.AddChatViewModel
import id.neotica.neoverse.presentation.screen.main.MainViewModel
import id.neotica.neoverse.presentation.screen.sharing.SharingViewModel
import id.neotica.profile.ProfileViewModel
import id.neotica.neoverse.utils.connectionchecker.AndroidConnectivityObserver
import id.neotica.network.AlexandriaRepositoryImpl
import id.neotica.network.AuraRepositoryImpl
import id.neotica.network.AuthRepositoryImpl
import id.neotica.network.ChatRepositoryImpl
import id.neotica.network.FeedRepositoryImpl
import id.neotica.network.FirebaseRepositoryImpl
import id.neotica.network.InventoryRepositoryImpl
import id.neotica.network.MarketRepositoryImpl
import id.neotica.network.MerchantRepositoryImpl
import id.neotica.network.PermissionRepositoryImpl
import id.neotica.network.RavenRepositoryImpl
import id.neotica.network.SuperAdminRepositoryImpl
import id.neotica.network.UserRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModules = module {
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::ChatRepositoryImpl) bind ChatRepository::class
    singleOf(::AuraRepositoryImpl) bind AuraRepository::class
    singleOf(::FriendDaoRepositoryImpl) bind FriendDaoRepository::class
    singleOf(::CurrentUserRepositoryImpl) bind CurrentUserRepository::class
    singleOf(::MarketRepositoryImpl) bind MarketRepository::class
    singleOf(::InventoryRepositoryImpl) bind InventoryRepository::class
    singleOf(::MerchantRepositoryImpl) bind MerchantRepository::class
    singleOf(::FirebaseRepositoryImpl) bind FirebaseRepository::class
    singleOf(::FeedRepositoryImpl) bind FeedRepository::class
    singleOf(::SuperAdminRepositoryImpl) bind SuperAdminRepository::class
    singleOf(::AndroidConnectivityObserver) bind ConnectivityObserver::class
    singleOf(::RavenRepositoryImpl) bind RavenRepository::class
    singleOf(::AlexandriaRepositoryImpl) bind AlexandriaRepository::class
    singleOf(::PermissionRepositoryImpl) bind PermissionRepository::class
}

val viewModelModules = module {
    includes(authModules)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddChatViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::FriendViewModel)
    viewModelOf(::DumpViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::SharingViewModel)
    viewModelOf(::CameraPreviewViewModel)
}

val useCaseModules = module {
    singleOf(::GetUserIdUseCase)
    singleOf(::GetUserNameUseCase)
    singleOf(::BuyHeroUseCase)
    singleOf(::GetAuraUseCase)
    singleOf(::BuyItemUseCase)
}

val domainModules = module {
//    factoryOf(::GetUserIdUseCase)
}