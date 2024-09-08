package io.github.cotrin8672.util

import com.mojang.blaze3d.vertex.PoseStack

fun <T> PoseStack.use(block: PoseStack.() -> T): T {
    pushPose()
    val result = block()
    popPose()
    return result
}
