package io.github.cotrin8672.blockentity

import com.google.common.collect.ImmutableList
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.content.kinetics.saw.CuttingRecipe
import com.simibubi.create.content.kinetics.saw.SawBlockEntity
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe
import com.simibubi.create.foundation.recipe.RecipeConditions
import com.simibubi.create.foundation.recipe.RecipeFinder
import com.simibubi.create.foundation.utility.BlockHelper
import com.simibubi.create.foundation.utility.Lang
import com.simibubi.create.foundation.utility.TreeCutter
import com.simibubi.create.foundation.utility.VecHelper
import com.simibubi.create.infrastructure.config.AllConfigs
import io.github.cotrin8672.entity.BlockBreaker
import io.github.cotrin8672.mixin.SawBlockEntityMixin
import joptsimple.internal.Strings
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import java.util.stream.Collectors
import kotlin.math.max
import kotlin.math.pow

class EnchantableSawBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
    private val delegate: EnchantableBlockEntityDelegate = EnchantableBlockEntityDelegate(),
) : SawBlockEntity(type, pos, state), EnchantableBlockEntity by delegate {
    private val fakePlayer by lazy {
        val nonNullLevel = checkNotNull(this.level)
        if (nonNullLevel is ServerLevel)
            BlockBreaker(nonNullLevel, this@EnchantableSawBlockEntity)
        else null
    }

    companion object {
        private val cuttingRecipesKey: Any = Any()
    }

    override fun getBreakSpeed(): Float {
        val efficiencyLevel = getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
        return super.getBreakSpeed() * (efficiencyLevel + 1)
    }

    override fun onBlockBroken(stateToBreak: BlockState) {
        val dynamicTree =
            TreeCutter.findDynamicTree(stateToBreak.block, breakingPos)
        if (dynamicTree.isPresent) {
            dynamicTree.get().destroyBlocks(level, fakePlayer) { pos: BlockPos, stack: ItemStack ->
                this.dropItemFromCutTree(pos, stack)
            }
            return
        }

        val vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakingPos), level!!.random, .125f)
        BlockHelper.destroyBlockAs(
            level, breakingPos, fakePlayer, fakePlayer?.mainHandItem ?: ItemStack.EMPTY, 1f
        ) { stack: ItemStack ->
            if (stack.isEmpty) return@destroyBlockAs
            if (!level!!.gameRules.getBoolean(GameRules.RULE_DOBLOCKDROPS)) return@destroyBlockAs
            if (level!!.restoringBlockSnapshots) return@destroyBlockAs

            val itemEntity = ItemEntity(level!!, vec.x, vec.y, vec.z, stack).apply {
                setDefaultPickUpDelay()
                deltaMovement = Vec3.ZERO
            }
            level!!.addFreshEntity(itemEntity)
        }
        TreeCutter.findTree(level, breakingPos).destroyBlocks(level, fakePlayer) { pos: BlockPos, stack: ItemStack ->
            this.dropItemFromCutTree(pos, stack)
        }
    }

    override fun start(inserted: ItemStack) {
        if (!canProcess()) return
        if (inventory.isEmpty) return
        if (level!!.isClientSide && !isVirtual) return

        val recipes = getRecipes()
        val valid = recipes.isNotEmpty()
        var time = 50 * (1 / (getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY).toDouble().pow(2.0) + 1))

        if (recipes.isEmpty()) {
            inventory.recipeDuration = 10f
            inventory.remainingTime = inventory.recipeDuration
            inventory.appliedRecipe = false
            sendData()
            return
        }

        if (valid) {
            (this@EnchantableSawBlockEntity as SawBlockEntityMixin).recipeIndex++
            if (recipeIndex >= recipes.size) recipeIndex = 0
        }

        val recipe = recipes[(this@EnchantableSawBlockEntity as SawBlockEntityMixin).recipeIndex]
        if (recipe is CuttingRecipe) {
            time = recipe.processingDuration * (1 / (getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY).toDouble()
                .pow(2.0) + 1))
        }

        inventory.remainingTime = (time * max(1.0, (inserted.count / 5).toDouble())).toFloat()
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
            if (AllConfigs.server().recipes.allowWoodcuttingOnSaw.get()) woodcuttingRecipeType.get() else null
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
        for (instance in delegate.enchantmentInstances) {
            val level = instance.level
            Lang.text(Strings.repeat(' ', 0))
                .add(instance.enchantment.getFullname(level).copy())
                .forGoggles(tooltip)
        }
        return true
    }

    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        delegate.enchantmentsTag = compound.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND.toInt())
        super.read(compound, clientPacket)
    }

    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        compound.remove(ItemStack.TAG_ENCH)
        delegate.enchantmentsTag?.let { compound.put(ItemStack.TAG_ENCH, it) }
        super.write(compound, clientPacket)
    }
}
