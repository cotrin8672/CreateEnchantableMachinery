package io.github.cotrin8672.createenchantablemachinery.forge

import io.github.cotrin8672.createenchantablemachinery.forge.platform.AlternativePlacementHelperImpl
import io.github.cotrin8672.createenchantablemachinery.forge.platform.ItemEntityDataHelperImpl
import io.github.cotrin8672.createenchantablemachinery.forge.platform.ItemStackHandlerHelperImpl
import io.github.cotrin8672.createenchantablemachinery.forge.platform.SideExecutorImpl
import io.github.cotrin8672.createenchantablemachinery.forge.platform.entity.BlockBreakerImpl
import io.github.cotrin8672.createenchantablemachinery.forge.platform.entity.ContraptionBlockBreakerImpl
import io.github.cotrin8672.createenchantablemachinery.forge.registrate.RegistrateHandlerImpl
import io.github.cotrin8672.createenchantablemachinery.platform.*
import io.github.cotrin8672.createenchantablemachinery.registrate.RegistrateHandler
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val registrateModule = module {
    singleOf(::RegistrateHandlerImpl) bind RegistrateHandler::class
}

val fakePlayerModule = module {
    singleOf(::BlockBreakerImpl) bind BlockBreaker::class
    singleOf(::ContraptionBlockBreakerImpl) bind ContraptionBlockBreaker::class
}

val alternativePlacementHelperModule = module {
    singleOf(::AlternativePlacementHelperImpl) bind AlternativePlacementHelper::class
}

val itemEntityDataHelperModule = module {
    singleOf(::ItemEntityDataHelperImpl) bind ItemEntityDataHelper::class
}

val sideExecutorModule = module {
    singleOf(::SideExecutorImpl) bind SideExecutor::class
}

val itemStackHandlerHelperModule = module {
    singleOf(::ItemStackHandlerHelperImpl) bind ItemStackHandlerHelper::class
}
