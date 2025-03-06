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
import io.github.cotrin8672.cem.content.entity.BlockBreaker
import io.github.cotrin8672.cem.mixin.SawBlockEntityMixin
import io.github.cotrin8672.cem.util.holderLookup
import io.github.cotrin8672.cem.util.nonNullLevel
import joptsimple.internal.Strings
import net.createmod.catnip.math.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.enchantment.Enchantment.getFullname
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import java.util.stream.Collectors
import kotlin.math.max

class EnchantableSawBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : SawBlockEntity(type, pos, state), EnchantableBlockEntity by EnchantableBlockEntityDelegate() {
    private val fakePlayer by lazy {
        if (this.level is ServerLevel)
            BlockBreaker(this.level as ServerLevel, this@EnchantableSawBlockEntity) else null
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
        val efficiency = holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY)
        val efficiencyLevel = getEnchantmentLevel(efficiency)
        return super.getBreakSpeed() * (efficiencyLevel + 1)
    }

    override fun onBlockBroken(stateToBreak: BlockState) {
        val dynamicTree = TreeCutter.findDynamicTree(stateToBreak.block, breakingPos)
        if (dynamicTree.isPresent) {
            dynamicTree.get().destroyBlocks(nonNullLevel, fakePlayer, this::dropItemFromCutTree)
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
            .destroyBlocks(nonNullLevel, fakePlayer, this::dropItemFromCutTree)
    }

    override fun start(inserted: ItemStack) {
        if (!canProcess()) return
        if (inventory.isEmpty) return
        if (nonNullLevel.isClientSide && !isVirtual) return

        val recipes = getRecipes()
        val valid = recipes.isNotEmpty()
        val efficiency = holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY)
        val efficiencyLevel = getEnchantmentLevel(efficiency)
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

        val recipe = recipes[recipeIndex].value()
        if (recipe is CuttingRecipe) time = recipe.processingDuration * efficiencyLevelModifier

        inventory.remainingTime = (time * max(1f, (inserted.count / 5f)))
        inventory.recipeDuration = inventory.remainingTime
        inventory.appliedRecipe = false
        sendData()
    }

    private fun getRecipes(): List<RecipeHolder<out Recipe<*>>> {
        val assemblyRecipe = SequencedAssemblyRecipe.getRecipe(
            nonNullLevel,
            inventory.getStackInSlot(0),
            AllRecipeTypes.CUTTING.getType(),
            CuttingRecipe::class.java
        )
        if (assemblyRecipe.isPresent && filtering.test(assemblyRecipe.get().value.getResultItem(nonNullLevel.registryAccess()))) {
            return ImmutableList.of(assemblyRecipe.get())
        }

        val types = RecipeConditions.isOfType(
            AllRecipeTypes.CUTTING.getType<RecipeInput, Recipe<RecipeInput>>(),
            if (AllConfigs.server().recipes.allowStonecuttingOnSaw.get()) RecipeType.STONECUTTING else null
        )

        val startedSearch = RecipeFinder.get(cuttingRecipesKey, nonNullLevel, types)
        return startedSearch.stream()
            .filter(RecipeConditions.outputMatchesFilter(filtering))
            .filter(RecipeConditions.firstIngredientMatches(inventory.getStackInSlot(0)))
            .filter { r: RecipeHolder<out Recipe<*>?>? -> !AllRecipeTypes.shouldIgnoreInAutomation(r) }
            .collect(Collectors.toList())
    }

    override fun addToGoggleTooltip(tooltip: MutableList<Component>, isPlayerSneaking: Boolean): Boolean {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking)

        if (getEnchantments().entrySet().isEmpty()) return false
        for (instance in getEnchantments().entrySet()) {
            CreateLang.text(Strings.repeat(' ', 0))
                .add(getFullname(instance.key, instance.intValue))
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        readEnchantments(compound, registries)
        super.read(compound, registries, clientPacket)
    }

    override fun write(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        writeEnchantments(compound, registries)
        super.write(compound, registries, clientPacket)
    }
}
