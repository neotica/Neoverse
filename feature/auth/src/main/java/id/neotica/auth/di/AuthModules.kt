package id.neotica.auth.di

import id.neotica.auth.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authModules = module {
    viewModelOf(::LoginViewModel)
}