package id.neotica.world.di

import id.neotica.alexandria.di.alexandriaModules
import id.neotica.raven.RavenViewModel
import id.neotica.tavern.TavernViewModel
import id.neotica.world.feat.inventory.InventoryViewModel
import id.neotica.world.feat.kanye.KanyeViewModel
import id.neotica.world.feat.leaderboards.LeaderBoardsViewModel
import id.neotica.world.feat.market.MarketViewModel
import id.neotica.world.feat.market.itemdetail.ItemDetailViewModel
import id.neotica.world.feat.market.merchant.MerchantViewModel
import id.neotica.world.feat.mining.MiningViewModel
import id.neotica.world.feat.superadmin.SuperAdminViewModel
import id.neotica.world.feat.world.WorldViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val worldModules = module {
    viewModelOf(::WorldViewModel)
    viewModelOf(::KanyeViewModel)
    viewModelOf(::TavernViewModel)
    viewModelOf(::MarketViewModel)
    viewModelOf(::ItemDetailViewModel)
    viewModelOf(::MerchantViewModel)
    viewModelOf(::InventoryViewModel)
    viewModelOf(::MiningViewModel)
    viewModelOf(::LeaderBoardsViewModel)
    viewModelOf(::SuperAdminViewModel)
    viewModelOf(::RavenViewModel)
    includes(alexandriaModules)
}