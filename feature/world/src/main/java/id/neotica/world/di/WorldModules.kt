package id.neotica.world.di

import id.neotica.world.feat.kanye.KanyeViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val worldModules = module {
    viewModelOf(::KanyeViewModel)
}