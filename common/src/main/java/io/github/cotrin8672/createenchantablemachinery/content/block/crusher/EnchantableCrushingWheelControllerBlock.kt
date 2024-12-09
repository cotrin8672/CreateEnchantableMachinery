package io.github.cotrin8672.createenchantablemachinery.content.block.crusher

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity
import com.simibubi.create.foundation.advancement.AllAdvancements
import com.simibubi.create.foundation.utility.Iterate
import io.github.cotrin8672.createenchantablemachinery.registrate.BlockEntityRegistration
import io.github.cotrin8672.createenchantablemachinery.registrate.BlockRegistration
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import kotlin.math.abs

class EnchantableCrushingWheelControllerBlock(
    properties: Properties,
) : CrushingWheelControllerBlock(properties) {
    override fun updateSpeed(state: BlockState, world: LevelAccessor, pos: BlockPos) {
        withBlockEntityDo(world, pos) { be: CrushingWheelControllerBlockEntity ->
            if (!state.getValue<Boolean>(VALID)) {
                if (be.crushingspeed != 0f) {
                    be.crushingspeed = 0f
                    be.sendData()
                }
                return@withBlockEntityDo
            }
            for (direction in Iterate.directions) {
                val neighbour = world.getBlockState(pos.relative(direction))
                val bool1 = !AllBlocks.CRUSHING_WHEEL.has(neighbour)
                val bool2 = !BlockRegistration.ENCHANTABLE_CRUSHING_WHEEL.has(neighbour)
                if (bool1 && bool2) continue
                if (neighbour.getValue(BlockStateProperties.AXIS) === direction.axis) continue
                val adjBE =
                    world.getBlockEntity(pos.relative(direction)) as? EnchantableCrushingWheelBlockEntity ?: continue
                be.crushingspeed = abs((adjBE.speed / 50f))
                be.sendData()

                adjBE.award(AllAdvancements.CRUSHING_WHEEL)
                if (adjBE.speed > 255) adjBE.award(AllAdvancements.CRUSHER_MAXED)

                break
            }
        }
    }

    override fun getName(): MutableComponent {
        return AllBlocks.CRUSHING_WHEEL_CONTROLLER.get().name
    }

    override fun getBlockEntityType(): BlockEntityType<out CrushingWheelControllerBlockEntity> {
        return BlockEntityRegistration.ENCHANTABLE_CRUSHING_WHEEL_CONTROLLER.get()
    }

    override fun asItem(): Item {
        return AllBlocks.CRUSHING_WHEEL_CONTROLLER.asItem()
    }
}
