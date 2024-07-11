package io.github.cotrin8672.block

import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.state.properties.IntegerProperty

object EnchantmentProperties {
    val EFFICIENCY_LEVEL: IntegerProperty =
        IntegerProperty.create("efficiency_level", 0, Enchantments.BLOCK_EFFICIENCY.maxLevel + 1)
    val FORTUNE_LEVEL: IntegerProperty =
        IntegerProperty.create("fortune_level", 0, Enchantments.BLOCK_FORTUNE.maxLevel + 1)
    val SILK_TOUCH_LEVEL: IntegerProperty =
        IntegerProperty.create("silk_touch_level", 0, 1)
}
