/*
 * Copyright 2018 Google LLC All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.sceneform.ux

import android.util.Log
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import io.github.sceneview.node.ModelNode

/**
 * Visualizes that a [BaseTransformableNode] is selected by rendering a footprint for the
 * selected node.
 */
class FootprintSelectionVisualizer : SelectionVisualizer {
    private val footprintNode = ModelNode(position = Vector3.zero())

    var footprintRenderable: ModelRenderable? = null
        set(value) {
            if (value == null) {
                field = null
                return
            }
            val copyRenderable = value.makeCopy()
            footprintNode.setRenderable(copyRenderable)
            copyRenderable.collisionShape = null
            field = copyRenderable
        }

    override fun applySelectionVisual(node: BaseTransformableNode) {
        footprintNode.parent = node
        val box = (node as? ModelNode)?.renderableInstance?.filamentAsset?.boundingBox
        val halfExtent = box?.halfExtent
        if (halfExtent != null) {
            val size = halfExtent.average().toFloat() * 16f // 2 * sqrt(2)/2 * 10
            Log.d("DADAYA", "applySelectionVisual: $size")
            footprintNode.scale = size
        }

    }

    override fun removeSelectionVisual(node: BaseTransformableNode) {
        footprintNode.parent = null
    }

}