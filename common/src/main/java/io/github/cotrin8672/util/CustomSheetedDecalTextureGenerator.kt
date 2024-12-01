package io.github.cotrin8672.util

import com.mojang.blaze3d.vertex.DefaultedVertexConsumer
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Matrix3f
import com.mojang.math.Matrix4f
import com.mojang.math.Vector3f
import com.mojang.math.Vector4f
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.core.Direction

@Environment(EnvType.CLIENT)
class CustomSheetedDecalTextureGenerator(
    private val delegate: VertexConsumer,
    pCameraPose: Matrix4f,
    pNormalPose: Matrix3f,
    private val textureScale: Float,
) : DefaultedVertexConsumer() {
    private val cameraInversePose: Matrix4f = Matrix4f(pCameraPose).apply { invert() }
    private val normalInversePose: Matrix3f = Matrix3f(pNormalPose).apply { invert() }
    private var x = 0f
    private var y = 0f
    private var z = 0f
    private var overlayU = 0
    private var overlayV = 0
    private var lightCoords = 0
    private var nx = 0f
    private var ny = 0f
    private var nz = 0f

    init {
        this.resetState()
    }

    private fun resetState() {
        this.x = 0.0f
        this.y = 0.0f
        this.z = 0.0f
        this.overlayU = 0
        this.overlayV = 10
        this.lightCoords = 15728880
        this.nx = 0.0f
        this.ny = 1.0f
        this.nz = 0.0f
    }

    override fun endVertex() {
        val vector3f = Vector3f(this.nx, this.ny, this.nz).apply { transform(normalInversePose) }
        val direction = Direction.getNearest(vector3f.x(), vector3f.y(), vector3f.z())
        val vector4f = Vector4f(this.x, this.y, this.z, 1.0f)
        vector4f.transform(this.cameraInversePose)
        vector4f.transform(Vector3f.YP.rotationDegrees(180f))
        vector4f.transform(Vector3f.XP.rotationDegrees(-90f))
        vector4f.transform(direction.rotation)
        val f = -vector4f.x() * this.textureScale
        val f1 = -vector4f.y() * this.textureScale
        delegate
            .vertex(x.toDouble(), y.toDouble(), z.toDouble())
            .color(1.0f, 1.0f, 1.0f, 1.0f)
            .uv(f, f1)
            .overlayCoords(this.overlayU, this.overlayV)
            .uv2(this.lightCoords)
            .normal(this.nx, this.ny, this.nz)
            .endVertex()
        this.resetState()
    }

    override fun vertex(pX: Double, pY: Double, pZ: Double): VertexConsumer {
        this.x = pX.toFloat()
        this.y = pY.toFloat()
        this.z = pZ.toFloat()
        return this
    }

    override fun color(pRed: Int, pGreen: Int, pBlue: Int, pAlpha: Int): VertexConsumer {
        return this
    }

    override fun uv(pU: Float, pV: Float): VertexConsumer {
        return this
    }

    override fun overlayCoords(pU: Int, pV: Int): VertexConsumer {
        this.overlayU = pU
        this.overlayV = pV
        return this
    }

    override fun uv2(pU: Int, pV: Int): VertexConsumer {
        this.lightCoords = pU or (pV shl 16)
        return this
    }

    override fun normal(pX: Float, pY: Float, pZ: Float): VertexConsumer {
        this.nx = pX
        this.ny = pY
        this.nz = pZ
        return this
    }
}
