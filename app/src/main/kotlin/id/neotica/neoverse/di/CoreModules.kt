package id.neotica.neoverse.di

import android.content.Context
import id.neotica.auth.di.authModules
import id.neotica.chat.ChatViewModel
import id.neotica.chat.client.chatModules
import id.neotica.domain.observer.ConnectivityObserver
import id.neotica.home.HomeViewModel
import id.neotica.home.addchat.AddChatViewModel
import id.neotica.ktor.ktorModule
import id.neotica.neoverse.presentation.screen.main.MainViewModel
import id.neotica.neoverse.utils.connectionchecker.AndroidConnectivityObserver
import id.neotica.profile.di.profileModules
import id.neotica.res.MR
import id.neotica.state.AppVariant
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun connectivityModule(context: Context) = module {
    single<ConnectivityObserver> { AndroidConnectivityObserver(context) }
}

val viewModelModules = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddChatViewModel)
    viewModelOf(::ChatViewModel)
    viewModelOf(::MainViewModel)
}

val appVariantModule = module {
    single {
        AppVariant(
            appName = "Neoverse",
            logo = MR.images.logo_neoverse
        )
    }
}

fun initializeKoin(context: Context, config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)
    modules(
        ktorModule,
        authModules,
        profileModules,
        chatModules,
        connectivityModule(context),
        viewModelModules,
        appVariantModule
    )
}
