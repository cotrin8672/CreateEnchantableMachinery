package io.github.cotrin8672.fabric

import io.github.cotrin8672.fabric.registrate.RegistrateHandlerImpl
import io.github.cotrin8672.registrate.RegistrateHandler
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val registrateModule = module {
    singleOf(::RegistrateHandlerImpl) bind RegistrateHandler::class
}
