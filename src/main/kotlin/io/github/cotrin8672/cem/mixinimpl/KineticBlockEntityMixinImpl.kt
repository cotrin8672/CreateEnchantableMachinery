package io.github.cotrin8672.cem.mixinimpl

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.foundation.utility.CreateLang
import joptsimple.internal.Strings
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.enchantment.Enchantment.getFullname
import net.minecraft.world.item.enchantment.ItemEnchantments
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object KineticBlockEntityMixinImpl {
    @JvmStatic
    fun addToGoogleTooltip(
        blockEntity: KineticBlockEntity,
        tooltip: MutableList<Component>,
        isPlayerSneaking: Boolean,
        cir: CallbackInfoReturnable<Boolean>,
    ) {
        val enchantments = blockEntity.components().getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)

        if (enchantments.entrySet().isNotEmpty()) {
            for (instance in enchantments.entrySet()) {
                CreateLang.text(Strings.repeat(' ', 0))
                    .add(getFullname(instance.key, instance.intValue))
                    .forGoggles(tooltip)
            }
        }
    }
}
