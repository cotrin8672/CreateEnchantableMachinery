package io.github.cotrin8672.cem.util

import com.zurrtum.create.content.contraptions.behaviour.MovementContext
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.util.Unit as McUnit

/**
 * Enchantments of a machine are kept in the vanilla `minecraft:enchantments` component of Create's own block entity.
 * On contraptions they are read back from the captured block entity data.
 */
object CemEnchantments {
    private val ENCHANTMENTS_KEY: String by lazy {
        BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(DataComponents.ENCHANTMENTS).toString()
    }

    @JvmStatic
    fun of(blockEntity: BlockEntity): ItemEnchantments =
        blockEntity.components().getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)

    @JvmStatic
    fun of(context: MovementContext): ItemEnchantments =
        (context as MovementContextExtension).cem_getEnchantments()

    @JvmStatic
    fun getLevel(enchantments: ItemEnchantments, key: ResourceKey<Enchantment>): Int =
        enchantments.entrySet().firstOrNull { it.key.`is`(key) }?.intValue ?: 0

    @JvmStatic
    fun efficiency(enchantments: ItemEnchantments): Int = getLevel(enchantments, Enchantments.EFFICIENCY)

    @JvmStatic
    fun efficiency(blockEntity: BlockEntity): Int = efficiency(of(blockEntity))

    @JvmStatic
    fun efficiency(context: MovementContext): Int = efficiency(of(context))

    /** An unbreakable tool carrying the machine's enchantments, so loot tables apply Fortune, Silk Touch etc. */
    @JvmStatic
    @JvmOverloads
    fun createTool(enchantments: ItemEnchantments, item: Item = Items.NETHERITE_PICKAXE): ItemStack =
        ItemStack(item).apply {
            set(DataComponents.UNBREAKABLE, McUnit.INSTANCE)
            set(DataComponents.ENCHANTMENTS, enchantments)
        }

    /** Key under which the enchantments travel in client sync data, see `SyncedBlockEntityMixin`. */
    const val SYNC_KEY = "cem:enchantments"

    /**
     * Reads enchantments from block entity NBT: a full save (server-side contraptions) keeps them in `components`,
     * while client sync data (client-side contraptions) has them under [SYNC_KEY].
     */
    @JvmStatic
    fun fromBlockEntityData(tag: CompoundTag?, registries: HolderLookup.Provider): ItemEnchantments {
        val encoded = tag?.get(SYNC_KEY)
            ?: tag?.getCompoundOrEmpty("components")?.get(ENCHANTMENTS_KEY)
            ?: return ItemEnchantments.EMPTY
        return ItemEnchantments.CODEC
            .parse(registries.createSerializationContext(NbtOps.INSTANCE), encoded)
            .result()
            .orElse(ItemEnchantments.EMPTY)
    }

    /** Applies enchantments received in a client sync packet. */
    @JvmStatic
    fun applySynced(blockEntity: BlockEntity, enchantments: ItemEnchantments) {
        if (of(blockEntity) == enchantments) return
        val components = DataComponentMap.builder()
        blockEntity.components().forEach { if (it.type() != DataComponents.ENCHANTMENTS) components.set(it) }
        if (!enchantments.isEmpty) components.set(DataComponents.ENCHANTMENTS, enchantments)
        blockEntity.setComponents(components.build())
        CemHooks.onClientEnchantmentsChanged(blockEntity)
    }

    private fun <T : Any> DataComponentMap.Builder.set(component: net.minecraft.core.component.TypedDataComponent<T>) {
        set(component.type(), component.value())
    }
}
