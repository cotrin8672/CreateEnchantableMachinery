package io.github.cotrin8672.createenchantablemachinery.fabric

import io.github.cotrin8672.createenchantablemachinery.fabric.platform.*
import io.github.cotrin8672.createenchantablemachinery.fabric.platform.entity.BlockBreakerImpl
import io.github.cotrin8672.createenchantablemachinery.fabric.platform.entity.ContraptionBlockBreakerImpl
import io.github.cotrin8672.createenchantablemachinery.fabric.registrate.RegistrateHandlerImpl
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

val fluidBehaviourModules = module {
    singleOf(::SmartFluidTankHelperImpl) bind SmartFluidTankHelper::class
    singleOf(::FillingBySpoutHelperImpl) bind FillingBySpoutHelper::class
    singleOf(::BlockSpoutingBehaviourHelperImpl) bind BlockSpoutingBehaviourHelper::class
    singleOf(::FluidFXHelperImpl) bind FluidFXHelper::class
    singleOf(::FluidRendererHelperImpl) bind FluidRendererHelper::class
    singleOf(::FluidVariantAttributesHelperImpl) bind FluidVariantAttributesHelper::class
}
