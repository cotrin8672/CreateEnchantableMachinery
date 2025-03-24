package io.github.cotrin8672.cem.registry

import com.simibubi.create.AllBlocks
import com.tterrag.registrate.util.entry.RegistryEntry
import io.github.cotrin8672.cem.Cem
import io.github.cotrin8672.cem.mixin.PonderTagBuilderMixin
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper
import net.createmod.ponder.api.registration.TagBuilder
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

object PonderTagsRegistration {
    val ENCHANTABLE_BLOCKS = Cem.asResource("enchantable_blocks")

    fun register(helper: PonderTagRegistrationHelper<ResourceLocation>) {
        val registry = helper.withKeyFunction { obj: RegistryEntry<*, *> -> obj.id }

        registry.registerTag(ENCHANTABLE_BLOCKS)
            .addToIndex()
            .itemStack(AllBlocks.MECHANICAL_DRILL.asStack().apply {
                set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
            })
            .title("Enchantable Blocks")
            .description("Components which can apply some enchantments")
            .register()

        registry.addToTag(ENCHANTABLE_BLOCKS).apply {
            add(AllBlocks.MECHANICAL_DRILL)
            add(AllBlocks.MECHANICAL_SAW)
            add(AllBlocks.MECHANICAL_HARVESTER)
            add(AllBlocks.MECHANICAL_PLOUGH)
            add(AllBlocks.ENCASED_FAN)
            add(AllBlocks.MILLSTONE)
            add(AllBlocks.CRUSHING_WHEEL)
            add(AllBlocks.MECHANICAL_PRESS)
            add(AllBlocks.MECHANICAL_MIXER)
            add(AllBlocks.MECHANICAL_ROLLER)
            add(AllBlocks.SPOUT)
        }
    }

    private fun TagBuilder.itemStack(stack: ItemStack): TagBuilder {
        (this as PonderTagBuilderMixin).setItemIcon(stack)
        return this
    }
}
