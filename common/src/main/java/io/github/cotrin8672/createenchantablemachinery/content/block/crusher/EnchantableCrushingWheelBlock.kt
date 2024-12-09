package io.github.cotrin8672.createenchantablemachinery.content.block.crusher

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock.VALID
import com.simibubi.create.foundation.block.IBE
import com.simibubi.create.foundation.utility.Iterate
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlock
import io.github.cotrin8672.createenchantablemachinery.content.block.EnchantableBlockEntity
import io.github.cotrin8672.createenchantablemachinery.registrate.BlockEntityRegistration
import io.github.cotrin8672.createenchantablemachinery.registrate.BlockRegistration
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import kotlin.math.sign

class EnchantableCrushingWheelBlock(properties: Properties) : CrushingWheelBlock(properties), EnchantableBlock {
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
            val ownEfficiencyLevel = if (ownBe is EnchantableBlockEntity) {
                ownBe.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
            } else 0
            val otherEfficiencyLevel = if (otherBe is EnchantableBlockEntity) {
                otherBe.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY)
            } else 0
            val ownSilkTouchLevel = if (ownBe is EnchantableBlockEntity)
                ownBe.getEnchantmentLevel(Enchantments.SILK_TOUCH) else 0
            val otherSilkTouchLevel = if (otherBe is EnchantableBlockEntity)
                otherBe.getEnchantmentLevel(Enchantments.SILK_TOUCH) else 0
            val tag = ListTag().apply {
                val efficiencyTag = CompoundTag()
                val silkTouchTag = CompoundTag()
                if (ownEfficiencyLevel + otherEfficiencyLevel > 0) {
                    efficiencyTag.putShort("lvl", (ownEfficiencyLevel + otherEfficiencyLevel).toShort())
                    efficiencyTag.putString(
                        "id",
                        EnchantmentHelper.getEnchantmentId(Enchantments.BLOCK_EFFICIENCY)?.toString() ?: "null"
                    )
                    add(efficiencyTag)
                }
                if (ownSilkTouchLevel + otherSilkTouchLevel > 0) {
                    silkTouchTag.putShort("lvl", (Mth.clamp(ownSilkTouchLevel + otherSilkTouchLevel, 0, 1)).toShort())
                    silkTouchTag.putString(
                        "id",
                        EnchantmentHelper.getEnchantmentId(Enchantments.SILK_TOUCH)?.toString() ?: "null"
                    )
                    add(silkTouchTag)
                }
            }
            if (controllerBe is EnchantableBlockEntity) controllerBe.setEnchantment(tag)
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

    override fun getBlockEntityType(): BlockEntityType<out CrushingWheelBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_CRUSHING_WHEEL.get()
    }

    @Deprecated("Deprecated in Java")
    override fun getDrops(blockState: BlockState, builder: LootParams.Builder): MutableList<ItemStack> {
        val blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY)
        val stack = ItemStack(AllBlocks.CRUSHING_WHEEL)
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
            }
        }
        return mutableListOf(stack)
    }

    override fun asItem(): Item {
        return AllBlocks.CRUSHING_WHEEL.asItem()
    }

    override fun getCloneItemStack(level: BlockGetter, pos: BlockPos, state: BlockState): ItemStack {
        val blockEntity = level.getBlockEntity(pos)
        val stack = ItemStack(AllBlocks.CRUSHING_WHEEL)
        if (blockEntity is EnchantableBlockEntity) {
            blockEntity.getEnchantments().forEach {
                stack.enchant(it.enchantment, it.level)
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
            blockEntity.setEnchantment(stack.enchantmentTags)
        }
    }

    override fun canApply(enchantment: Enchantment): Boolean {
        return enchantment == Enchantments.BLOCK_EFFICIENCY || enchantment == Enchantments.SILK_TOUCH
    }
}
