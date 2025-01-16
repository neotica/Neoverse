package id.neotica.alexandria.di

import id.neotica.alexandria.ktor.GoetiaService
import id.neotica.alexandria.screen.goetia.GoetiaViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val alexandriaModules = module {
    viewModelOf(::GoetiaViewModel)
    singleOf(::GoetiaService)
}