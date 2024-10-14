package io.github.cotrin8672.fabric

import io.github.cotrin8672.content.entity.FakePlayerFactory
import io.github.cotrin8672.fabric.content.entity.FakePlayerFactoryImpl
import io.github.cotrin8672.fabric.registrate.RegistrateHandlerImpl
import io.github.cotrin8672.fabric.util.AlternativePlacementHelperImpl
import io.github.cotrin8672.registrate.RegistrateHandler
import io.github.cotrin8672.util.AlternativePlacementHelper
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
