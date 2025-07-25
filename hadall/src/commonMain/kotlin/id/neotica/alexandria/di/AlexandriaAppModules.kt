package id.neotica.alexandria.di

import id.neotica.auth.di.authModules
import id.neotica.database.CurrentUserRepositoryImpl
import id.neotica.database.FriendDaoRepositoryImpl
import id.neotica.database.platformModule
import id.neotica.database.provideDatabaseModule
import id.neotica.domain.local.CurrentUserRepository
import id.neotica.domain.local.FriendDaoRepository
import id.neotica.domain.usecase.GetUserNameUseCase
import id.neotica.ktor.ktorModule
import id.neotica.profile.di.profileModules
import id.neotica.res.MR
import id.neotica.state.AppVariant
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun initializeKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            ktorModule,
            platformModule(),
            provideDatabaseModule,
            repositoryModules,
            viewModelModules,
            useCaseModules,
            appVariantModule
        )
    }
}

val repositoryModules = module {
    singleOf(::FriendDaoRepositoryImpl) bind FriendDaoRepository::class
    singleOf(::CurrentUserRepositoryImpl) bind CurrentUserRepository::class
}

val viewModelModules = module {
    includes(authModules)
    includes(alexandriaModules)
    includes(profileModules)
}

val useCaseModules = module {
    singleOf(::GetUserNameUseCase)
}

val appVariantModule = module {
    single {
        AppVariant(
            appName = "Alexandria",
            logo = MR.images.logo_alexandria
        )
    }
}