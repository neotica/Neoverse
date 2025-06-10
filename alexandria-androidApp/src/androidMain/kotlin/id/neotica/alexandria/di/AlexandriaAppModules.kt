package id.neotica.alexandria.di

import android.content.Context
import id.neotica.auth.di.authModules
import id.neotica.database.CurrentUserRepositoryImpl
import id.neotica.database.FriendDaoRepositoryImpl
import id.neotica.database.databaseModule
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.remote.AlexandriaAdminRepository
import id.neotica.domain.remote.AuthRepository
import id.neotica.domain.remote.FirebaseRepository
import id.neotica.domain.remote.PermissionRepository
import id.neotica.domain.remote.UserRepository
import id.neotica.domain.remote.admin.WarningRepository
import id.neotica.domain.remote.alexandria.BookRepository
import id.neotica.domain.remote.alexandria.ChapterRepository
import id.neotica.domain.remote.alexandria.ContentRepository
import id.neotica.domain.remote.usecase.TokenProvider
import id.neotica.domain.usecase.GetUserIdUseCase
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.network.AlexandriaAdminRepositoryImpl
import id.neotica.network.AuthRepositoryImpl
import id.neotica.network.FirebaseRepositoryImpl
import id.neotica.network.PermissionRepositoryImpl
import id.neotica.network.UserRepositoryImpl
import id.neotica.network.admin.WarningRepositoryImpl
import id.neotica.network.alexandria.BookRepositoryImpl
import id.neotica.network.alexandria.ChapterRepositoryImpl
import id.neotica.network.alexandria.ContentRepositoryImpl
import id.neotica.network.micro.ktorModule
import id.neotica.network.usecase.FirebaseTokenProvider
import id.neotica.profile.ProfileViewModel
import id.neotica.rickpository.res.MR
import id.neotica.state.AppVariant
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun initializeKoin(context: Context) {
    startKoin {
        modules(
            ktorModule,
            databaseModule(context),
            repositoryModules,
            viewModelModules,
            useCaseModules,
            appVariantModule
        )
    }
}

val repositoryModules = module {
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::FriendDaoRepositoryImpl) bind FriendDaoRepository::class
    singleOf(::CurrentUserRepositoryImpl) bind CurrentUserRepository::class
    singleOf(::FirebaseRepositoryImpl) bind FirebaseRepository::class
    singleOf(::AlexandriaAdminRepositoryImpl) bind AlexandriaAdminRepository::class
    singleOf(::PermissionRepositoryImpl) bind PermissionRepository::class
    singleOf(::BookRepositoryImpl) bind BookRepository::class
    singleOf(::ChapterRepositoryImpl) bind ChapterRepository::class
    singleOf(::ContentRepositoryImpl) bind ContentRepository::class
    singleOf(::WarningRepositoryImpl) bind WarningRepository::class
}

val viewModelModules = module {
    includes(authModules)
    includes(alexandriaModules)
    viewModelOf(::ProfileViewModel)
}

val useCaseModules = module {
    singleOf(::GetUserIdUseCase)
    singleOf(::GetUserNameUseCase)
    singleOf(::FirebaseTokenProvider) bind TokenProvider::class
}

val appVariantModule = module {
    single {
        AppVariant(
            appName = "Alexandria",
            logo = MR.images.logo_alexandria
        )
    }
}