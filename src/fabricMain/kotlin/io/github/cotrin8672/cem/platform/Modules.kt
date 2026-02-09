package io.github.cotrin8672.cem.platform

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val fabricPlatformModule = module {
    singleOf(::RegistrateProviderImpl) bind RegistrateProvider::class
    singleOf(::BlockEntityBuilderHelperImpl) bind BlockEntityBuilderHelper::class
}
