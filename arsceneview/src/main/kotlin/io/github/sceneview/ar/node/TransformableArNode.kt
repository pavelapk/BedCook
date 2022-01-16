package io.github.sceneview.ar.node

import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.ux.RotationController
import com.google.ar.sceneform.ux.ScaleController
import com.google.ar.sceneform.ux.TransformationSystem
import com.google.ar.sceneform.ux.TranslationController
import io.github.sceneview.node.NodeParent
import java.util.*


class TransformableArNode(
    transformationSystem: TransformationSystem,
    position: Vector3 = defaultPosition,
    rotationQuaternion: Quaternion = defaultRotation,
    scales: Vector3 = defaultScales,
    parent: NodeParent? = null
) : ArNode(position, rotationQuaternion, scales, parent, transformationSystem) {

    constructor(transformationSystem: TransformationSystem, anchor: Anchor) : this(
        transformationSystem
    ) {
        this.anchor = anchor
    }

    constructor(transformationSystem: TransformationSystem, hitResult: HitResult) : this(
        transformationSystem,
        hitResult.createAnchor()
    )

    private val translationController =
        TranslationController(this, transformationSystem.dragRecognizer).apply {
            allowedPlaneTypes = EnumSet.of(Plane.Type.HORIZONTAL_UPWARD_FACING)
        }

    //    private val scaleController = ScaleController(this, transformationSystem.pinchRecognizer)
    private val rotationController = RotationController(this, transformationSystem.twistRecognizer)

    init {
        addTransformationController(translationController)
//        addTransformationController(scaleController)
        addTransformationController(rotationController)
    }

}
