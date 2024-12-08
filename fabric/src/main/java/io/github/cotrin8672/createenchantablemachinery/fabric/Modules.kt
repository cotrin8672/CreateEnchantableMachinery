package io.github.cotrin8672.createenchantablemachinery.fabric

import io.github.cotrin8672.createenchantablemachinery.content.entity.FakePlayerFactory
import io.github.cotrin8672.createenchantablemachinery.fabric.entity.FakePlayerFactoryImpl
import io.github.cotrin8672.createenchantablemachinery.fabric.registrate.RegistrateHandlerImpl
import io.github.cotrin8672.createenchantablemachinery.fabric.util.AlternativePlacementHelperImpl
import io.github.cotrin8672.createenchantablemachinery.fabric.util.ItemEntityDataHelperImpl
import io.github.cotrin8672.createenchantablemachinery.fabric.util.ItemStackHandlerHelperImpl
import io.github.cotrin8672.createenchantablemachinery.fabric.util.SideExecutorImpl
import io.github.cotrin8672.createenchantablemachinery.registrate.RegistrateHandler
import io.github.cotrin8672.createenchantablemachinery.util.interfaces.AlternativePlacementHelper
import io.github.cotrin8672.createenchantablemachinery.util.interfaces.ItemEntityDataHelper
import io.github.cotrin8672.createenchantablemachinery.util.interfaces.ItemStackHandlerHelper
import io.github.cotrin8672.createenchantablemachinery.util.interfaces.SideExecutor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val registrateModule = module {
    singleOf(::RegistrateHandlerImpl) bind RegistrateHandler::class
}

val fakePlayerModule = module {
    singleOf(::FakePlayerFactoryImpl) bind FakePlayerFactory::class
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
