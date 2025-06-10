package id.neotica.neoverse.di

import android.content.Context
import id.neotica.auth.di.authModules
import id.neotica.chat.ChatViewModel
import id.neotica.database.CurrentUserRepositoryImpl
import id.neotica.database.FriendDaoRepositoryImpl
import id.neotica.database.databaseModule
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.observer.ConnectivityObserver
import id.neotica.domain.remote.AlexandriaAdminRepository
import id.neotica.domain.remote.AuraRepository
import id.neotica.domain.remote.AuthRepository
import id.neotica.domain.remote.ChatRepository
import id.neotica.domain.remote.FeedRepository
import id.neotica.domain.remote.FirebaseRepository
import id.neotica.domain.remote.InventoryRepository
import id.neotica.domain.remote.MarketRepository
import id.neotica.domain.remote.MerchantRepository
import id.neotica.domain.remote.PermissionRepository
import id.neotica.domain.remote.RavenRepository
import id.neotica.domain.remote.SuperAdminRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.remote.alexandria.BookRepository
import id.neotica.domain.remote.alexandria.ChapterRepository
import id.neotica.domain.remote.alexandria.ContentRepository
import id.neotica.domain.remote.usecase.TokenProvider
import id.neotica.domain.usecase.BuyHeroUseCase
import id.neotica.domain.usecase.BuyItemUseCase
import id.neotica.domain.usecase.GetAuraUseCase
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.friend.FriendViewModel
import id.neotica.home.HomeViewModel
import id.neotica.home.addchat.AddChatViewModel
import id.neotica.neoverse.presentation.screen.camerax.CameraPreviewViewModel
import id.neotica.neoverse.presentation.screen.main.MainViewModel
import id.neotica.neoverse.presentation.screen.sharing.SharingViewModel
import id.neotica.neoverse.utils.connectionchecker.AndroidConnectivityObserver
import id.neotica.network.AlexandriaAdminRepositoryImpl
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
import id.neotica.network.alexandria.BookRepositoryImpl
import id.neotica.network.alexandria.ChapterRepositoryImpl
import id.neotica.network.alexandria.ContentRepositoryImpl
import id.neotica.network.micro.ktorModule
import id.neotica.network.usecase.FirebaseTokenProvider
import id.neotica.profile.ProfileViewModel
import id.neotica.state.AppVariant
import id.neotica.ui.R
import id.neotica.world.di.worldModules
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
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
    singleOf(::RavenRepositoryImpl) bind RavenRepository::class
    singleOf(::AlexandriaAdminRepositoryImpl) bind AlexandriaAdminRepository::class
    singleOf(::PermissionRepositoryImpl) bind PermissionRepository::class
    singleOf(::BookRepositoryImpl) bind BookRepository::class
    singleOf(::ChapterRepositoryImpl) bind ChapterRepository::class
    singleOf(::ContentRepositoryImpl) bind ContentRepository::class
}

fun connectivityModule(context: Context) = module {
    single<ConnectivityObserver> { AndroidConnectivityObserver(context) }
}

val viewModelModules = module {
    includes(authModules)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddChatViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::FriendViewModel)
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
    singleOf(::FirebaseTokenProvider) bind TokenProvider::class
}

val appVariantModule = module {
    single {
        AppVariant(
            appName = "Neoverse",
            logo = R.drawable.logo_app
        )
    }
}

val domainModules = module {
//    factoryOf(::GetUserIdUseCase)
}

fun initializeKoin(context: Context) = startKoin {
    modules(
        ktorModule,
        databaseModule(context),
        connectivityModule(context),
        repositoryModules,
        viewModelModules,
        domainModules,
        useCaseModules,
        worldModules,
        appVariantModule
    )
}