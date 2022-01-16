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

import com.google.ar.sceneform.PickHitResult
import android.view.MotionEvent
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import io.github.sceneview.node.Node
import io.github.sceneview.node.NodeParent
import java.util.ArrayList

/**
 * Base class for nodes that can be transformed using gestures from [TransformationSystem].
 */
abstract class BaseTransformableNode(
    position: Vector3 = defaultPosition,
    rotationQuaternion: Quaternion = defaultRotation,
    scales: Vector3 = defaultScales,
    parent: NodeParent? = null,
    private val transformationSystem: TransformationSystem? = null
) : Node(position, rotationQuaternion, scales, parent) {
    private val controllers = ArrayList<BaseTransformationController<*>>()

    init {
        onTouched = { hitTestResult: PickHitResult?, motionEvent: MotionEvent? ->
            onTap(hitTestResult, motionEvent)
        }
    }

    /** Returns true if any of the transformation controllers are actively transforming this node.  */
    fun isTransforming(): Boolean {
        for (i in controllers.indices) {
            if (controllers[i].isTransforming) {
                return true
            }
        }
        return false
    }

    /** Returns true if this node is currently selected by the TransformationSystem.  */
    fun isSelected(): Boolean {
        return transformationSystem?.selectedNode === this
    }

    /**
     * Sets this as the selected node in the TransformationSystem if there is no currently selected
     * node or if the currently selected node is not actively being transformed.
     *
     * @see BaseTransformableNode.isTransforming
     *
     * @return true if the node was successfully selected
     */
    fun select(): Boolean {
        return transformationSystem?.selectNode(this) ?: false
    }

    fun onTap(pickHitResult: PickHitResult?, motionEvent: MotionEvent?) {
        select()
    }

    protected fun addTransformationController(
        transformationController: BaseTransformationController<*>
    ) {
        controllers.add(transformationController)
    }

    protected fun removeTransformationController(
        transformationController: BaseTransformationController<*>
    ) {
        controllers.remove(transformationController)
    }
}