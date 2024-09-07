package id.neotica.neoverse.di

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import id.neotica.neoverse.data.local.FriendTable
import id.neotica.neoverse.data.local.repo.FriendRoomRepositoryImpl
import id.neotica.neoverse.data.repo.AuthRepositoryImpl
import id.neotica.neoverse.data.repo.ChatRepositoryImpl
import id.neotica.neoverse.data.repo.GameRepositoryImpl
import id.neotica.neoverse.data.repo.UserRepositoryImpl
import id.neotica.neoverse.domain.AuthRepository
import id.neotica.neoverse.domain.ChatRepository
import id.neotica.neoverse.domain.GameRepository
import id.neotica.neoverse.domain.UserRepository
import id.neotica.neoverse.presentation.screen.auth.login.LoginViewModel
import id.neotica.neoverse.presentation.screen.auth.register.RegisterViewModel
import id.neotica.neoverse.presentation.screen.auth.verify.VerifyEmailViewModel
import id.neotica.neoverse.presentation.screen.chat.ChatViewModel
import id.neotica.neoverse.presentation.screen.friend.FriendViewModel
import id.neotica.neoverse.presentation.screen.game.GameViewModel
import id.neotica.neoverse.presentation.screen.home.HomeViewModel
import id.neotica.neoverse.presentation.screen.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    factory { get<FriendTable>().friendDao() }
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = FriendTable::class.java,
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
    singleOf(::FriendRoomRepositoryImpl)
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
}

val domainModules = module {
//    factoryOf(::GetUserIdUseCase)
}

val useCaseModules = module {

}