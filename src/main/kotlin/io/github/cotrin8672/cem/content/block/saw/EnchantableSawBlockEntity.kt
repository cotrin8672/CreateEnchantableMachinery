package io.github.cotrin8672.cem.content.block.saw

import com.google.common.collect.ImmutableList
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.kinetics.saw.CuttingRecipe
import com.simibubi.create.content.kinetics.saw.SawBlockEntity
import com.simibubi.create.content.kinetics.saw.TreeCutter
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour
import com.simibubi.create.foundation.recipe.RecipeConditions
import com.simibubi.create.foundation.recipe.RecipeFinder
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.utility.CreateLang
import com.simibubi.create.infrastructure.config.AllConfigs
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntityDelegate
import io.github.cotrin8672.cem.mixin.SawBlockEntityMixin
import io.github.cotrin8672.cem.util.EnchantedItemFactory
import io.github.cotrin8672.cem.util.nonNullLevel
import joptsimple.internal.Strings
import net.createmod.catnip.math.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import java.util.stream.Collectors
import kotlin.math.max

class EnchantableSawBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SawBlockEntity(type, pos, state), EnchantableBlockEntity by EnchantableBlockEntityDelegate() {
    override fun <T : Any?> getCapability(cap: Capability<T>): LazyOptional<T> {
        return super.getCapability(cap)
    }

    private val filtering: FilteringBehaviour
        get() = (this as SawBlockEntityMixin).filtering
    private var recipeIndex: Int
        get() = (this as SawBlockEntityMixin).recipeIndex
        set(value) {
            (this as SawBlockEntityMixin).recipeIndex = value
        }
    private val cuttingRecipesKey: Any
        get() = (this as SawBlockEntityMixin).cuttingRecipesKey

    override fun getBreakSpeed(): Float {
        val efficiencyLevel = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
        return super.getBreakSpeed() * (efficiencyLevel + 1)
    }

    override fun onBlockBroken(stateToBreak: BlockState) {
        val dynamicTree = TreeCutter.findDynamicTree(stateToBreak.block, breakingPos)
        val enchantedItem = EnchantedItemFactory.getToolForBlock(nonNullLevel, breakingPos, getEnchantmentTag())

        if (dynamicTree.isPresent) {
            dynamicTree.get().destroyBlocks(nonNullLevel, enchantedItem, null, this::dropItemFromCutTree)
            return
        }

        // copy from BlockBreakingKineticBlockEntity#onBlockBroken
        val vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), nonNullLevel.random, .125f)
        BlockHelper.destroyBlock(nonNullLevel, breakingPos, 1f) { stack: ItemStack ->
            if (stack.isEmpty) return@destroyBlock
            if (!nonNullLevel.gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS)) return@destroyBlock
            if (nonNullLevel.restoringBlockSnapshots) return@destroyBlock

            val itementity = ItemEntity(nonNullLevel, vec.x, vec.y, vec.z, stack)
            itementity.setDefaultPickUpDelay()
            itementity.deltaMovement = Vec3.ZERO
            nonNullLevel.addFreshEntity(itementity)
        }

        TreeCutter.findTree(nonNullLevel, breakingPos, stateToBreak)
            .destroyBlocks(nonNullLevel, enchantedItem, null, this::dropItemFromCutTree)
    }

    override fun start(inserted: ItemStack) {
        if (!canProcess()) return
        if (inventory.isEmpty) return
        if (nonNullLevel.isClientSide && !isVirtual) return

        val recipes = getRecipes()
        val valid = recipes.isNotEmpty()
        val efficiencyLevel = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
        val efficiencyLevelModifier = max(0.1f, 1f - (efficiencyLevel * 0.1f))
        var time = 50 * efficiencyLevelModifier

        if (recipes.isEmpty()) {
            inventory.recipeDuration = 10f
            inventory.remainingTime = inventory.recipeDuration
            inventory.appliedRecipe = false
            sendData()
            return
        }

        if (valid) {
            recipeIndex++
            if (recipeIndex >= recipes.size) recipeIndex = 0
        }

        val recipe = recipes[recipeIndex]
        if (recipe is CuttingRecipe) time = recipe.processingDuration * efficiencyLevelModifier

        inventory.remainingTime = (time * max(1f, (inserted.count / 5f)))
        inventory.recipeDuration = inventory.remainingTime
        inventory.appliedRecipe = false
        sendData()
    }

    private fun getRecipes(): List<Recipe<*>> {
        val assemblyRecipe = SequencedAssemblyRecipe.getRecipe(
            level, inventory.getStackInSlot(0),
            AllRecipeTypes.CUTTING.getType(),
            CuttingRecipe::class.java
        )
        if (assemblyRecipe.isPresent && (this@EnchantableSawBlockEntity as SawBlockEntityMixin).filtering.test(
                assemblyRecipe.get().getResultItem(level!!.registryAccess())
            )
        ) return ImmutableList.of(assemblyRecipe.get())

        val types = RecipeConditions.isOfType(
            AllRecipeTypes.CUTTING.getType(),
            if (AllConfigs.server().recipes.allowStonecuttingOnSaw.get()) RecipeType.STONECUTTING else null,
        )

        val startedSearch = RecipeFinder.get(cuttingRecipesKey, level, types)
        return startedSearch.stream()
            .filter(RecipeConditions.outputMatchesFilter((this@EnchantableSawBlockEntity as SawBlockEntityMixin).filtering))
            .filter(RecipeConditions.firstIngredientMatches(inventory.getStackInSlot(0)))
            .filter { r: Recipe<*>? -> !AllRecipeTypes.shouldIgnoreInAutomation(r) }
            .collect(Collectors.toList())
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)
        for (instance in getEnchantments()) {
            val level = instance.level
            CreateLang.text(Strings.repeat(' ', 0))
                .add(instance.enchantment.getFullname(level).copy())
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        readEnchantments(compound)
        super.read(compound, clientPacket)
    }

    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        compound.remove(ItemStack.TAG_ENCH)
        writeEnchantments(compound)
        super.write(compound, clientPacket)
    }
}
