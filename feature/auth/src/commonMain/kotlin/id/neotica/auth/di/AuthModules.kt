package id.neotica.auth.di

import id.neotica.auth.data.AuthRepositoryImpl
import id.neotica.auth.data.GetCurrentUserTokenUseCaseImpl
import id.neotica.auth.data.SessionRepositoryImpl
import id.neotica.auth.data.authPlatformModule
import id.neotica.auth.domain.AuthRepository
import id.neotica.auth.domain.GetCurrentUserTokenUseCase
import id.neotica.auth.presentation.login.LoginViewModel
import id.neotica.auth.presentation.register.RegisterViewModel
import id.neotica.auth.verify.VerifyEmailViewModel
import id.neotica.domain.remote.usecase.TokenProvider
import id.neotica.domain.session.SessionRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModules = module {
    includes(authPlatformModule())
    singleOf(::AuthRepositoryImpl).bind(AuthRepository::class)
    singleOf(::SessionRepositoryImpl).bind(SessionRepository::class)
    single<TokenProvider> { get<SessionRepositoryImpl>() }
    singleOf(::GetCurrentUserTokenUseCaseImpl).bind(GetCurrentUserTokenUseCase::class)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::VerifyEmailViewModel)
}
