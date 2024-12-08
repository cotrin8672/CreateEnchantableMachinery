package io.github.cotrin8672.createenchantablemachinery.content

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer

// MIT License
//
// Copyright (c) 2024 Mrbysco
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

class EnchantedRenderType private constructor(
    pName: String,
    pFormat: VertexFormat,
    pMode: VertexFormat.Mode,
    pBufferSize: Int,
    pAffectsCrumbling: Boolean,
    pSortOnUpload: Boolean,
    pSetupState: Runnable,
    pClearState: Runnable,
) :
    RenderType(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState) {
    companion object {
        private val CUSTOM_POLYGON_OFFSET_LAYERING = LayeringStateShard(
            "polygon_offset_layering", {
                RenderSystem.polygonOffset(-0.25f, -10.0f)
                RenderSystem.enablePolygonOffset()
            }, {
                RenderSystem.polygonOffset(0.0f, 0.0f)
                RenderSystem.disablePolygonOffset()
            }
        )

        val GLINT: RenderType = create(
            "glint",
            DefaultVertexFormat.POSITION_TEX,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            false,
            CompositeState.builder()
                .setShaderState(RENDERTYPE_GLINT_SHADER)
                .setTextureState(TextureStateShard(ItemRenderer.ENCHANT_GLINT_LOCATION, true, false))
                .setWriteMaskState(COLOR_WRITE)
                .setCullState(NO_CULL)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setTransparencyState(GLINT_TRANSPARENCY)
                .setTexturingState(GLINT_TEXTURING)
                .setLayeringState(CUSTOM_POLYGON_OFFSET_LAYERING)
                .createCompositeState(false)
        )
    }
}
