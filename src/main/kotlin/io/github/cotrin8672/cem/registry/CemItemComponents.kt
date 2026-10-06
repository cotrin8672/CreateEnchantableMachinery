package io.github.cotrin8672.cem.registry

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.enchantment.Enchantable

/** Makes the machine items enchantable. Which enchantments apply comes from the `minecraft:enchantable/...` item tags. */
object CemItemComponents {
    fun register() {
        DefaultItemComponentEvents.MODIFY.register { context ->
            context.modify(CemMachines.items) { builder, _ ->
                builder.set(DataComponents.ENCHANTABLE, Enchantable(CemMachines.ENCHANTMENT_VALUE))
            }
        }
    }
}
