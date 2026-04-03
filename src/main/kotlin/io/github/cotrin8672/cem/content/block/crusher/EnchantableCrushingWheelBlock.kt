package io.github.cotrin8672.cem.content.block.crusher

import com.simibubi.create.AllBlocks
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock.VALID
import com.simibubi.create.content.schematics.requirement.ItemRequirement
import com.simibubi.create.foundation.block.IBE
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.registry.BlockEntityRegistration
import io.github.cotrin8672.cem.registry.BlockRegistration
import io.github.cotrin8672.cem.util.handleSneakWrenchWithSourceItem
import io.github.cotrin8672.cem.util.holderLookup
import net.createmod.catnip.data.Iterate
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.sign

class EnchantableCrushingWheelBlock(properties: Properties) : CrushingWheelBlock(properties),
    SpecialBlockItemRequirement {
    @Deprecated("Deprecated in Java")
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        for (direction in Iterate.directions) {
            if (direction.axis === state.getValue(AXIS)) continue
            if (
                AllBlocks.CRUSHING_WHEEL_CONTROLLER.has(level.getBlockState(pos.relative(direction))) ||
                BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER.has(level.getBlockState(pos.relative(direction)))
            )
                level.removeBlock(pos.relative(direction), isMoving)
        }
        IBE.onRemove(state, level, pos, newState)
    }

    override fun updateControllers(state: BlockState, level: Level?, pos: BlockPos, side: Direction) {
        if (side.axis == state.getValue(AXIS)) return
        if (level == null) return

        val controllerPos = pos.relative(side)
        val otherWheelPos = pos.relative(side, 2)

        val controllerExists = AllBlocks.CRUSHING_WHEEL_CONTROLLER.has(level.getBlockState(controllerPos))
                || BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER.has(level.getBlockState(controllerPos))
        val controllerIsValid = controllerExists && level.getBlockState(controllerPos).getValue(VALID)
        val controllerOldDirection = if (controllerExists)
            level.getBlockState(controllerPos).getValue(CrushingWheelControllerBlock.FACING)
        else null
        var controllerShouldExist = false
        var controllerShouldBeValid = false
        var controllerNewDirection = Direction.DOWN

        val otherState = level.getBlockState(otherWheelPos)
        if (AllBlocks.CRUSHING_WHEEL.has(otherState) || BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL.has(otherState)) {
            controllerShouldExist = true

            val be = getBlockEntity(level, pos)
            val otherBE = getBlockEntity(level, otherWheelPos)

            if (be != null && otherBE != null && (be.speed > 0) != (otherBE.speed > 0) && be.speed != 0f) {
                val wheelAxis = state.getValue(AXIS)
                val sideAxis = side.axis
                val controllerADO = Math.round(sign(be.speed)) * side.axisDirection.step
                val controllerDirVec = Vec3(
                    if (wheelAxis == Direction.Axis.X) 1.0 else 0.0,
                    if (wheelAxis == Direction.Axis.Y) 1.0 else 0.0,
                    if (wheelAxis == Direction.Axis.Z) 1.0 else 0.0,
                ).cross(
                    Vec3(
                        if (sideAxis == Direction.Axis.X) 1.0 else 0.0,
                        if (sideAxis == Direction.Axis.Y) 1.0 else 0.0,
                        if (sideAxis == Direction.Axis.Z) 1.0 else 0.0,
                    )
                )
                controllerNewDirection = Direction.getNearest(
                    controllerDirVec.x * controllerADO,
                    controllerDirVec.y * controllerADO,
                    controllerDirVec.z * controllerADO,
                )
                controllerShouldBeValid = true
            }
            if (otherState.getValue(AXIS) != state.getValue(AXIS)) controllerShouldExist = false
        }

        if (!controllerShouldExist) {
            if (controllerExists)
                level.setBlockAndUpdate(controllerPos, Blocks.AIR.defaultBlockState())
            return
        }

        if (!controllerExists) {
            if (!level.getBlockState(controllerPos).canBeReplaced()) return
            level.setBlockAndUpdate(
                controllerPos, BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER.defaultState
                    .setValue(VALID, controllerShouldBeValid)
                    .setValue(CrushingWheelControllerBlock.FACING, controllerNewDirection)
            )
        } else if (controllerIsValid != controllerShouldBeValid || controllerOldDirection != controllerNewDirection) {
            level.setBlockAndUpdate(
                controllerPos, BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER.defaultState
                    .setValue(VALID, controllerShouldBeValid)
                    .setValue(CrushingWheelControllerBlock.FACING, controllerNewDirection)
            )
        }

        if (controllerExists) {
            val ownBe = level.getBlockEntity(pos)
            val otherBe = level.getBlockEntity(otherWheelPos)
            val controllerBe = level.getBlockEntity(controllerPos)

            val ownEnchantable = ownBe as? EnchantableBlockEntity
            val otherEnchantable = otherBe as? EnchantableBlockEntity
            val mayHaveAnyEnchantments = ownEnchantable?.getEnchantments()?.isEmpty == false
                    || otherEnchantable?.getEnchantments()?.isEmpty == false
            var effectiveFortuneRounded = 0f
            var silkTouchWheelCount = 0

            val itemEnchantments = ItemEnchantments.Mutable(ItemEnchantments.EMPTY).apply {
                if (mayHaveAnyEnchantments) {
                    val enchantmentLookup = ownBe!!.holderLookup(Registries.ENCHANTMENT)
                    val efficiency = enchantmentLookup.getOrThrow(Enchantments.EFFICIENCY)
                    val fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE)
                    val silkTouch = enchantmentLookup.getOrThrow(Enchantments.SILK_TOUCH)

                    val ownEfficiencyLevel = ownEnchantable?.getEnchantmentLevel(efficiency) ?: 0
                    val otherEfficiencyLevel = otherEnchantable?.getEnchantmentLevel(efficiency) ?: 0
                    val totalEfficiencyLevel = ownEfficiencyLevel + otherEfficiencyLevel
                    if (totalEfficiencyLevel > 0) {
                        set(efficiency, totalEfficiencyLevel)
                    }

                    val ownFortuneLevel = ownEnchantable?.getEnchantmentLevel(fortune) ?: 0
                    val otherFortuneLevel = otherEnchantable?.getEnchantmentLevel(fortune) ?: 0
                    val effectiveFortune = (ownFortuneLevel + otherFortuneLevel) / 2.0
                    effectiveFortuneRounded = (kotlin.math.round(effectiveFortune * 100) / 100.0).toFloat()
                    val displayFortuneLevel = effectiveFortuneRounded.toInt().coerceIn(0, 3)
                    if (displayFortuneLevel > 0) {
                        set(fortune, displayFortuneLevel)
                    }

                    val ownSilkTouchLevel = ownEnchantable?.getEnchantmentLevel(silkTouch) ?: 0
                    val otherSilkTouchLevel = otherEnchantable?.getEnchantmentLevel(silkTouch) ?: 0
                    silkTouchWheelCount = (if (ownSilkTouchLevel > 0) 1 else 0) + (if (otherSilkTouchLevel > 0) 1 else 0)
                    if (silkTouchWheelCount > 0) {
                        set(silkTouch, 1)
                    }
                }
            }.toImmutable()
            if (controllerBe is EnchantableBlockEntity) controllerBe.setEnchantment(itemEnchantments)
            if (controllerBe is EnchantableCrushingWheelControllerBlockEntity) {
                controllerBe.setEffectiveFortuneLevel(effectiveFortuneRounded)
                controllerBe.setSilkTouchWheelCount(silkTouchWheelCount)
            }
        }

        BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER.get().updateSpeed(
            level.getBlockState(controllerPos),
            level,
            controllerPos
        )
    }

    override fun getName(): MutableComponent {
        return AllBlocks.CRUSHING_WHEEL.get().name
    }

    override fun getDescriptionId(): String {
        return AllBlocks.CRUSHING_WHEEL.get().descriptionId
    }

    override fun getBlockEntityType(): BlockEntityType<out CrushingWheelBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_CRUSHING_WHEEL.get()
    }

    override fun asItem(): Item {
        return AllBlocks.CRUSHING_WHEEL.asItem()
    }

    override fun getCloneItemStack(
        state: BlockState,
        target: HitResult,
        level: LevelReader,
        pos: BlockPos,
        player: Player,
    ): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val sourceItem = (blockEntity as? EnchantableBlockEntity)?.getSourceItem() ?: AllBlocks.CRUSHING_WHEEL.asItem()
        val stack = ItemStack(sourceItem)
        if (blockEntity is EnchantableBlockEntity) {
            val enchantments = blockEntity.getEnchantments().entrySet()
            enchantments.forEach {
                stack.enchant(it.key, it.intValue)
            }
        }
        return stack
    }

    override fun setPlacedBy(
        worldIn: Level,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        stack: ItemStack,
    ) {
        super.setPlacedBy(worldIn, pos, state, placer, stack)
        val blockEntity = worldIn.getBlockEntity(pos)
        if (blockEntity is EnchantableBlockEntity) {
            val enchantments = stack.get(DataComponents.ENCHANTMENTS) ?: ItemEnchantments.EMPTY
            blockEntity.setEnchantment(enchantments)
            blockEntity.setSourceItem(stack.item)
            val components = DataComponentMap.builder()
                .addAll(blockEntity.components())
                .set(DataComponents.ENCHANTMENTS, stack.get(DataComponents.ENCHANTMENTS) ?: ItemEnchantments.EMPTY)
                .build()
            blockEntity.setComponents(components)
        }
    }

    override fun getRequiredItems(state: BlockState, blockEntity: BlockEntity?): ItemRequirement {
        val sourceItem = (blockEntity as? EnchantableBlockEntity)?.getSourceItem() ?: AllBlocks.CRUSHING_WHEEL.asItem()
        val stack = ItemStack(sourceItem)
        if (blockEntity is EnchantableBlockEntity) {
            val enchantments = blockEntity.getEnchantments()
            stack.set(DataComponents.ENCHANTMENTS, enchantments)
        }
        val strictRequirement = ItemRequirement.StrictNbtStackRequirement(stack, ItemRequirement.ItemUseType.CONSUME)
        return ItemRequirement(strictRequirement)
    }

    override fun onSneakWrenched(state: BlockState, context: UseOnContext): InteractionResult {
        return handleSneakWrenchWithSourceItem(state, context)
    }
}
