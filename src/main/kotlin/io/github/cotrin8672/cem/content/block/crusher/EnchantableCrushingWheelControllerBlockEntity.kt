package io.github.cotrin8672.cem.content.block.crusher

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.damageTypes.CreateDamageSources
import com.simibubi.create.foundation.item.ItemHelper
import com.simibubi.create.foundation.sound.SoundScapes
import com.simibubi.create.foundation.sound.SoundScapes.AmbienceGroup
import com.simibubi.create.infrastructure.config.AllConfigs
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntity
import io.github.cotrin8672.cem.content.block.EnchantableBlockEntityDelegate
import io.github.cotrin8672.cem.mixin.CrushingWheelControllerBlockEntityMixin
import io.github.cotrin8672.cem.util.holderLookup
import io.github.cotrin8672.cem.util.nonNullLevel
import io.github.cotrin8672.cem.util.smartBlockEntityTick
import net.createmod.catnip.math.VecHelper
import net.createmod.catnip.platform.CatnipServices
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import java.util.*
import kotlin.math.max

class EnchantableCrushingWheelControllerBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
) : CrushingWheelControllerBlockEntity(type, pos, state),
    EnchantableBlockEntity by EnchantableBlockEntityDelegate(),
    IHaveGoggleInformation {
    private var entityUUID: UUID?
        get() = (this as CrushingWheelControllerBlockEntityMixin).entityUUID
        set(value) {
            (this as CrushingWheelControllerBlockEntityMixin).entityUUID = value
        }

    override fun addBehaviours(behaviours: MutableList<BlockEntityBehaviour>) {
        behaviours.add(DirectBeltInputBehaviour(this).onlyInsertWhen(this::supportsDirectBeltInput))
    }

    private fun supportsDirectBeltInput(side: Direction): Boolean {
        val direction = blockState.getValue(CrushingWheelControllerBlock.FACING)
        return direction == Direction.DOWN || direction == side
    }

    override fun tick() {
        smartBlockEntityTick()

        if (searchForEntity) {
            searchForEntity = false
            val search = nonNullLevel.getEntities(null as Entity?, AABB(blockPos)) {
                entityUUID?.equals(it.uuid) == true
            }
            if (search.isEmpty()) clear()
            else processingEntity = search[0]
        }

        if (!isOccupied) return
        if (crushingspeed == 0f) return
        if (nonNullLevel.isClientSide) CatnipServices.PLATFORM.executeOnClientOnly { Runnable { this.tickAudio() } }

        val speed = crushingspeed * 4

        val centerPos = VecHelper.getCenterOf(worldPosition)
        val facing = blockState.getValue(CrushingWheelControllerBlock.FACING)
        val offset = facing.axisDirection.step
        val outSpeed = Vec3(
            (if (facing.axis == Direction.Axis.X) 0.25 else 0.0) * offset,
            if (offset == 1) (if (facing.axis == Direction.Axis.Y) 0.5 else 0.0) else 0.0,
            if (facing.axis == Direction.Axis.Z) 0.25 else 0.0
        )
        val outPos = centerPos.add(
            if (facing.axis == Direction.Axis.X) 0.55 * offset else 0.0,
            if (facing.axis == Direction.Axis.Y) 0.55 * offset else 0.0,
            if (facing.axis == Direction.Axis.Z) 0.55 * offset else 0.0
        )
        if (!hasEntity()) {
            val efficiency = holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY)
            val efficiencyModifier = 1 + 0.2f * getEnchantmentLevel(efficiency)
            val processingSpeed = Mth.clamp(
                speed * efficiencyModifier / (if (!inventory.appliedRecipe) Mth.log2(inventory.getStackInSlot(0).count) else 1),
                0.25f,
                20f
            )
            inventory.remainingTime -= processingSpeed
            spawnParticles(inventory.getStackInSlot(0))

            if (nonNullLevel.isClientSide) return
            if (inventory.remainingTime < 20 && !inventory.appliedRecipe) {
                applyRecipe()
                inventory.appliedRecipe = true
                nonNullLevel.sendBlockUpdated(worldPosition, blockState, blockState, 2 or 16)
                return
            }
            if (inventory.remainingTime > 0) return
            inventory.remainingTime = 0f

            // Output Items
            if (facing != Direction.UP) {
                val nextPos = worldPosition.offset(
                    if (facing.axis == Direction.Axis.X) 1 * offset else 0,
                    -1,
                    if (facing.axis == Direction.Axis.Z) 1 * offset else 0
                )
                val behaviour = BlockEntityBehaviour.get(nonNullLevel, nextPos, DirectBeltInputBehaviour.TYPE)
                if (behaviour != null) {
                    var changed = false
                    if (!behaviour.canInsertFromSide(facing)) return
                    for (slot in 0 until inventory.slots) {
                        val stack = inventory.getStackInSlot(slot)
                        if (stack.isEmpty) continue
                        val remainder = behaviour.handleInsertion(stack, facing, false)
                        if (remainder.equals(stack)) continue
                        inventory.setStackInSlot(slot, remainder)
                        changed = true
                    }
                    if (changed) {
                        setChanged()
                        sendData()
                    }
                    return
                }
            }

            // Eject Items
            for (slot in 0 until inventory.slots) {
                val stack = inventory.getStackInSlot(slot)
                if (stack.isEmpty) continue
                val entityIn = ItemEntity(nonNullLevel, outPos.x, outPos.y, outPos.z, stack)
                entityIn.deltaMovement = outSpeed
                entityIn.persistentData.put("BypassCrushingWheel", NbtUtils.writeBlockPos(worldPosition))
                nonNullLevel.addFreshEntity(entityIn)
            }
            inventory.clear()
            nonNullLevel.sendBlockUpdated(worldPosition, blockState, blockState, 2 or 16)
            return
        }

        processingEntity?.let {
            if (!it.isAlive || !it.boundingBox.intersects(AABB(worldPosition).inflate(0.5))) {
                clear()
                return
            }
        }

        processingEntity?.let {
            var xMotion = ((worldPosition.x + 0.5f) - it.x) / 2f
            var zMotion = ((worldPosition.z + 0.5f) - it.z) / 2f
            if (it.isShiftKeyDown) {
                zMotion = 0.0
                xMotion = zMotion
            }
            val movement = max(-speed / 4.0, -0.5) * -offset
            it.deltaMovement = Vec3(
                if (facing.axis == Direction.Axis.X) movement else xMotion,
                if (facing.axis == Direction.Axis.Y) movement else 0.0,
                if (facing.axis == Direction.Axis.Z) movement else zMotion
            )
            if (nonNullLevel.isClientSide) return

            if (it !is ItemEntity) {
                val entityOutPos = outPos.add(
                    if (facing.axis === Direction.Axis.X) 0.5 * offset else 0.0,
                    if (facing.axis === Direction.Axis.Y) 0.5 * offset else 0.0,
                    if (facing.axis === Direction.Axis.Z) 0.5 * offset else 0.0
                )
                val crusherDamage = AllConfigs.server().kinetics.crushingDamage.get()

                if (it is LivingEntity) {
                    if ((it.health - crusherDamage <= 0) && (it.hurtTime <= 0))
                        it.setPos(entityOutPos.x, entityOutPos.y, entityOutPos.z)
                }
                it.hurt(CreateDamageSources.crush(level), crusherDamage.toFloat())
                if (!it.isAlive) it.setPos(entityOutPos.x, entityOutPos.y, entityOutPos.z)
                return
            } else {
                it.setPickUpDelay(20)
                if (facing.axis == Direction.Axis.Y) {
                    if (it.y * -offset < (centerPos.y - 0.25f) * -offset)
                        intakeItem(it)
                } else if (facing.axis == Direction.Axis.Z) {
                    if (it.z * -offset < (centerPos.z - 0.25f) * -offset)
                        intakeItem(it)
                } else {
                    if (it.x * -offset < (centerPos.x - 0.25f) * -offset)
                        intakeItem(it)
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun tickAudio() {
        val pitch = Mth.clamp((crushingspeed / 256f) + 0.45f, 0.85f, 1f)
        if (entityUUID == null && inventory.getStackInSlot(0).isEmpty) return
        SoundScapes.play(AmbienceGroup.CRUSHING, worldPosition, pitch)
    }

    private fun intakeItem(itemEntity: ItemEntity) {
        inventory.clear()
        inventory.setStackInSlot(0, itemEntity.item.copy())
        itemInserted(inventory.getStackInSlot(0))
        itemEntity.discard()
        nonNullLevel.sendBlockUpdated(worldPosition, blockState, blockState, 2 or 16)
    }

    override fun read(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        readEnchantments(compound, registries)
        super.read(compound, registries, clientPacket)
    }

    override fun write(compound: CompoundTag, registries: HolderLookup.Provider, clientPacket: Boolean) {
        writeEnchantments(compound, registries)
        super.write(compound, registries, clientPacket)
        if (hasEntity()) entityUUID?.let { compound.put("Entity", NbtUtils.createUUID(it)) }
    }

    override fun startCrushing(entity: Entity) {
        processingEntity = entity
        entityUUID = entity.uuid
    }

    override fun clear() {
        processingEntity = null
        entityUUID = null
    }

    private fun applyRecipe() {
        val recipe = findRecipe()

        val list = mutableListOf<ItemStack>()
        if (recipe.isPresent) {
            val rolls = inventory.getStackInSlot(0).count
            inventory.clear()
            for (roll in 0 until rolls) {
                val rolledResults = recipe.get().value.rollResults()
                for (stack in rolledResults) {
                    ItemHelper.addToList(stack, list)
                }
            }
            var slot = 0
            while (slot < list.size && slot + 1 < inventory.slots) {
                inventory.setStackInSlot(slot + 1, list[slot])
                slot++
            }
        } else {
            inventory.clear()
        }
    }

    private fun itemInserted(stack: ItemStack) {
        val recipe = findRecipe()
        inventory.remainingTime = if (recipe.isPresent) recipe.get().value.processingDuration.toFloat() else 100f
        inventory.appliedRecipe = false
    }
}
