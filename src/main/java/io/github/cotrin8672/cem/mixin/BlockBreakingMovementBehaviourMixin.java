package io.github.cotrin8672.cem.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import io.github.cotrin8672.cem.util.EnchantedItemFactory;
import kotlin.jvm.optionals.OptionalsKt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

@Mixin(value = BlockBreakingMovementBehaviour.class, remap = false)
public class BlockBreakingMovementBehaviourMixin {
    @Unique
    private static final Map<MovementContext, ItemStack> enchantedTools = new WeakHashMap<>();

    @Unique
    private static final Codec<DataComponentMap> BLOCK_ENTITY_COMPONENTS_CODEC = DataComponentMap.CODEC
            .optionalFieldOf("components", DataComponentMap.EMPTY)
            .codec();

    @WrapOperation(
            method = "tickBreaker",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/kinetics/base/BlockBreakingMovementBehaviour;getBlockBreakingSpeed("
                            + "Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;)F",
                    remap = false
            )
    )
    private float cem$tickBreaker(BlockBreakingMovementBehaviour instance, MovementContext context, Operation<Float> original) {
        if (context == null) return original.call(instance, context);

        ItemStack enchantedTool = enchantedTools.computeIfAbsent(
                context,
                ctx -> {
                    var ops = ctx.world.registryAccess().createSerializationContext(NbtOps.INSTANCE);
                    DataComponentMap components = BLOCK_ENTITY_COMPONENTS_CODEC
                            .parse(ops, ctx.blockEntityData)
                            .result()
                            .orElse(DataComponentMap.EMPTY);
                    ItemEnchantments enchantments = components.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

                    return EnchantedItemFactory.INSTANCE.getPickaxeItemStack(enchantments);
                }
        );

        var holderLookup = context.world.holderLookup(Registries.ENCHANTMENT);
        var holder = OptionalsKt.getOrNull(holderLookup.get(Enchantments.EFFICIENCY));
        if (holder == null) return original.call(instance, context);
        var efficiencyLevel = enchantedTool.getEnchantmentLevel(holder);

        return original.call(instance, context) * (efficiencyLevel + 1);
    }

    @WrapOperation(
            method = "destroyBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/utility/BlockHelper;destroyBlock("
                            + "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;"
                            + "FLjava/util/function/Consumer;)V",
                    remap = false
            )
    )
    private void cem$destroyBlock(
            Level world,
            BlockPos pos,
            float effectChance,
            Consumer<ItemStack> droppedItemCallback,
            Operation<Void> original,
            MovementContext context,
            BlockPos breakingPos
    ) {
        if (context == null) original.call(world, pos, effectChance, droppedItemCallback);

        ItemStack enchantedTool = enchantedTools.computeIfAbsent(
                context,
                ctx -> {
                    var ops = ctx.world.registryAccess().createSerializationContext(NbtOps.INSTANCE);
                    DataComponentMap components = BLOCK_ENTITY_COMPONENTS_CODEC
                            .parse(ops, ctx.blockEntityData)
                            .result()
                            .orElse(DataComponentMap.EMPTY);
                    ItemEnchantments enchantments = components.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

                    return EnchantedItemFactory.INSTANCE.getPickaxeItemStack(enchantments);
                }
        );

        BlockHelper.destroyBlockAs(world, pos, null, enchantedTool, effectChance, droppedItemCallback);
    }
}
