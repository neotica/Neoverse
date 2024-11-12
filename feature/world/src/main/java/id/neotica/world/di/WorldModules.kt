package id.neotica.world.di

import id.neotica.world.feat.game.GameViewModel
import id.neotica.world.feat.inventory.InventoryViewModel
import id.neotica.world.feat.kanye.KanyeViewModel
import id.neotica.world.feat.market.MarketViewModel
import id.neotica.world.feat.market.itemdetail.ItemDetailViewModel
import id.neotica.world.feat.market.merchant.MerchantViewModel
import id.neotica.world.feat.mining.MiningViewModel
import id.neotica.world.feat.world.WorldViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val worldModules = module {
    viewModelOf(::WorldViewModel)
    viewModelOf(::KanyeViewModel)
    viewModelOf(::GameViewModel)
    viewModelOf(::MarketViewModel)
    viewModelOf(::ItemDetailViewModel)
    viewModelOf(::MerchantViewModel)
    viewModelOf(::InventoryViewModel)
    viewModelOf(::MiningViewModel)
}