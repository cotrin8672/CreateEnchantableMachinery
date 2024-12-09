package io.github.cotrin8672.fabric

import io.github.cotrin8672.fabric.platform.AlternativePlacementHelperImpl
import io.github.cotrin8672.fabric.platform.ItemEntityDataHelperImpl
import io.github.cotrin8672.fabric.platform.ItemStackHandlerHelperImpl
import io.github.cotrin8672.fabric.platform.SideExecutorHelperImpl
import io.github.cotrin8672.fabric.platform.entity.BlockBreakerImpl
import io.github.cotrin8672.fabric.platform.entity.ContraptionBlockBreakerImpl
import io.github.cotrin8672.fabric.registrate.RegistrateHandlerImpl
import io.github.cotrin8672.platform.*
import io.github.cotrin8672.registrate.RegistrateHandler
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

val sideExecutorHelperModule = module {
    singleOf(::SideExecutorHelperImpl) bind SideExecutorHelper::class
}

val itemStackHandlerHelperModule = module {
    singleOf(::ItemStackHandlerHelperImpl) bind ItemStackHandlerHelper::class
}
