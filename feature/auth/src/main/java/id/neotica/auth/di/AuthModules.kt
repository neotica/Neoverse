package id.neotica.auth.di

import id.neotica.auth.login.LoginViewModel
import id.neotica.auth.register.RegisterViewModel
import id.neotica.auth.verify.VerifyEmailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModules = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::VerifyEmailViewModel)
}