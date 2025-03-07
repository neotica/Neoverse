package id.neotica.alexandria.di

import id.neotica.alexandria.screen.bloodyage.BloodyAgeViewModel
import id.neotica.alexandria.screen.goetia.GoetiaViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val alexandriaModules = module {
    viewModelOf(::GoetiaViewModel)
    viewModelOf(::BloodyAgeViewModel)
}