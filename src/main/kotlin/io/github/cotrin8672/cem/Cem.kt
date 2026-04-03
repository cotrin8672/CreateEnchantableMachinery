package io.github.cotrin8672.cem

import com.simibubi.create.foundation.data.CreateRegistrate
import com.tterrag.registrate.util.entry.BlockEntry
import io.github.cotrin8672.cem.config.CemConfig
import io.github.cotrin8672.cem.config.ModConfigs
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.block.crusher.EnchantableCrushingWheelControllerBlockEntity
import io.github.cotrin8672.cem.content.block.millstone.EnchantableMillstoneBlockEntity
import io.github.cotrin8672.cem.content.block.saw.EnchantableSawBlockEntity
import io.github.cotrin8672.cem.content.block.spout.EnchantableSpoutBlockEntity
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import io.github.cotrin8672.cem.registry.BlockRegistration
import io.github.cotrin8672.cem.util.EnchantableBlockMapping
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.event.level.BlockDropsEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@EventBusSubscriber(modid = Cem.MOD_ID)
@Mod(Cem.MOD_ID)
class Cem(container: ModContainer) {
    companion object {
        const val MOD_ID = "createenchantablemachinery"
        val REGISTRATE: CreateRegistrate = CreateRegistrate.create(MOD_ID)

        fun asResource(path: String): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
        }

        @JvmStatic
        @SubscribeEvent
        fun registerCapabilities(event: RegisterCapabilitiesEvent) {
            EnchantableSawBlockEntity.registerCapabilities(event)
            EnchantableCrushingWheelControllerBlockEntity.registerCapabilities(event)
            EnchantableMillstoneBlockEntity.registerCapabilities(event)
            EnchantableSpoutBlockEntity.registerCapabilities(event)
        }
    }

    init {
        MOD_BUS.addListener(this::registerEnchantableBlockMapping)
        NeoForge.EVENT_BUS.addListener(this::onBlockDrops)
        MOD_BUS.register(this::class.java)
        REGISTRATE.registerEventListeners(MOD_BUS)
        BlockRegistration.register()
        BlockEntityRegistration.register()
        container.registerConfig(ModConfig.Type.CLIENT, CemConfig.CONFIG_SPEC)
        ModConfigs.register(ModLoadingContext.get(), container)
    }

    private fun registerEnchantableBlockMapping(event: FMLCommonSetupEvent) {
        rebuildEnchantableBlockMapping()
    }

    private fun onBlockDrops(event: BlockDropsEvent) {
        val enchantableBlockEntity = event.blockEntity as? EnchantableBlockEntity ?: return
        val sourceItem = enchantableBlockEntity.getSourceItem() ?: return
        val defaultDroppedItem = event.state.block.asItem()

        for (drop in event.drops) {
            val oldStack = drop.item
            if (oldStack.item != defaultDroppedItem) continue

            val newStack = ItemStack(sourceItem, oldStack.count)
            val enchantments = oldStack.get(DataComponents.ENCHANTMENTS)
            if (enchantments != null && !enchantments.isEmpty) {
                newStack.set(DataComponents.ENCHANTMENTS, enchantments)
            }
            drop.item = newStack
        }
    }

    private fun rebuildEnchantableBlockMapping() {
        EnchantableBlockMapping.clear()
        for (block in BuiltInRegistries.BLOCK) {
            val path = BuiltInRegistries.BLOCK.getKey(block).path
            val mappedEntry = resolveEnchantableEntryBySuffix(path) ?: continue
            EnchantableBlockMapping.register(block, mappedEntry)
        }
    }

    private fun resolveEnchantableEntryBySuffix(path: String): BlockEntry<*>? {
        if (matchesMachinePath(path, "mechanical_drill")) return BlockRegistration.ENCHANTABLE_MECHANICAL_DRILL
        if (matchesMachinePath(path, "mechanical_saw")) return BlockRegistration.ENCHANTABLE_MECHANICAL_SAW
        if (matchesMachinePath(path, "mechanical_harvester")) return BlockRegistration.ENCHANTABLE_MECHANICAL_HARVESTER
        if (matchesMachinePath(path, "encased_fan")) return BlockRegistration.ENCHANTABLE_ENCASED_FAN
        if (matchesMachinePath(path, "millstone")) return BlockRegistration.ENCHANTABLE_MILLSTONE
        if (matchesMachinePath(path, "crushing_wheel")) return BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL
        if (matchesMachinePath(path, "mechanical_plough")) return BlockRegistration.ENCHANTABLE_MECHANICAL_PLOUGH
        if (matchesMachinePath(path, "mechanical_mixer")) return BlockRegistration.ENCHANTABLE_MECHANICAL_MIXER
        if (matchesMachinePath(path, "mechanical_press")) return BlockRegistration.ENCHANTABLE_MECHANICAL_PRESS
        if (matchesMachinePath(path, "mechanical_roller")) return BlockRegistration.ENCHANTABLE_MECHANICAL_ROLLER
        if (matchesMachinePath(path, "spout")) return BlockRegistration.ENCHANTABLE_SPOUT
        return null
    }

    private fun matchesMachinePath(path: String, suffix: String): Boolean {
        return path == suffix || path.endsWith("_$suffix")
    }
}
