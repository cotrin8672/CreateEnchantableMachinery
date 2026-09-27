package io.github.cotrin8672.cem.registry

import io.github.cotrin8672.cem.Cem
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object CemEnchantmentEffectComponents {
    val REGISTER = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Cem.MOD_ID)
    
    val BLOCK_BREAK_SPEED: DeferredHolder<DataComponentType<*>, DataComponentType<EnchantmentValueEffect>> =
        REGISTER.register("block_break_speed", Supplier {
            DataComponentType.builder<EnchantmentValueEffect>()
                .persistent(EnchantmentValueEffect.CODEC)
                .build()
        })
}
