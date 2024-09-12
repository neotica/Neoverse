package id.neotica.neoverse.di

import androidx.room.Room
import id.neotica.neoverse.data.local.MainDatabase
import id.neotica.neoverse.data.local.repo.CurrentUserRepositoryImpl
import id.neotica.neoverse.data.local.repo.FriendDaoRepositoryImpl
import id.neotica.neoverse.data.remote.repo.AuthRepositoryImpl
import id.neotica.neoverse.data.remote.repo.ChatRepositoryImpl
import id.neotica.neoverse.data.remote.repo.GameRepositoryImpl
import id.neotica.neoverse.data.remote.repo.UserRepositoryImpl
import id.neotica.neoverse.domain.local.CurrentUserRepository
import id.neotica.neoverse.domain.local.FriendDaoRepository
import id.neotica.neoverse.domain.remote.AuthRepository
import id.neotica.neoverse.domain.remote.ChatRepository
import id.neotica.neoverse.domain.remote.GameRepository
import id.neotica.neoverse.domain.remote.UserRepository
import id.neotica.neoverse.presentation.screen.auth.login.LoginViewModel
import id.neotica.neoverse.presentation.screen.auth.register.RegisterViewModel
import id.neotica.neoverse.presentation.screen.auth.verify.VerifyEmailViewModel
import id.neotica.neoverse.presentation.screen.chat.ChatViewModel
import id.neotica.neoverse.presentation.screen.friend.FriendViewModel
import id.neotica.neoverse.presentation.screen.game.GameViewModel
import id.neotica.neoverse.presentation.screen.home.DumpViewModel
import id.neotica.neoverse.presentation.screen.home.HomeViewModel
import id.neotica.neoverse.presentation.screen.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    factory { get<MainDatabase>().friendDao() }
    factory { get<MainDatabase>().currentUserDao() }
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = MainDatabase::class.java,
            name = "Friend.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}

val repositoryModules = module {
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>()}
    singleOf(::UserRepositoryImpl) { bind<UserRepository>()}
    singleOf(::ChatRepositoryImpl) bind ChatRepository::class
    singleOf(::GameRepositoryImpl) bind GameRepository::class
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
    viewModelOf(::GameViewModel)
    viewModelOf(::VerifyEmailViewModel)
    viewModelOf(::DumpViewModel)
}

val domainModules = module {
//    factoryOf(::GetUserIdUseCase)
}

val useCaseModules = module {

}