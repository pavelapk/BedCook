package io.github.sceneview.node

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.utilities.ChangeId
import io.github.sceneview.model.GlbLoader

/**
 * ### A Node represents a transformation within the scene graph's hierarchy.
 *
 * This node contains a renderable for the rendering engine to render.
 *
 * Each node can have an arbitrary number of child nodes and one parent. The parent may be
 * another node, or the scene.
 */
open class ModelNode(
    position: Vector3 = defaultPosition,
    rotationQuaternion: Quaternion = defaultRotation,
    scales: Vector3 = defaultScales,
    parent: NodeParent? = null
) : Node(position, rotationQuaternion, scales, parent) {

    // Rendering fields.
    private var renderableId: Int = ChangeId.EMPTY_ID

    /**
     * ### The [RenderableInstance] to display.
     *
     * If [collisionShape] is not set, then [Renderable.getCollisionShape] is used to detect
     * collisions for this [Node].
     *
     * The renderable is usually a 3D model.
     * If null, this node's current renderable will be removed.
     */
    var renderableInstance: RenderableInstance? = null
        set(value) {
            if (field != value) {
                field?.renderer = null
                field?.destroy()
                field = value
                value?.renderer = if (shouldBeRendered) renderer else null
                onRenderableChanged()
            }
        }

    val renderable: Renderable?
        get() = renderableInstance?.renderable

    override var isRendered: Boolean
        get() = super.isRendered
        set(value) {
            renderableInstance?.renderer = if (value) renderer else null
            super.isRendered = value
        }

    var onModelLoaded: ((renderableInstance: RenderableInstance) -> Unit)? = null
    var onError: ((exception: Exception) -> Unit)? = null

    constructor(
        renderableInstance: RenderableInstance,
        parent: NodeParent? = null,
        position: Vector3 = defaultPosition,
        rotationQuaternion: Quaternion = defaultRotation,
        scales: Vector3 = defaultScales
    ) : this(position, rotationQuaternion, scales) {
        this.renderableInstance = renderableInstance
    }

    /**
     * @param modelGlbFileLocation the glb file location:
     * - A relative asset file location *models/mymodel.glb*
     * - An android resource from the res folder *context.getResourceUri(R.raw.mymodel)*
     * - A File path *Uri.fromFile(myModelFile).path*
     * - An http or https url *https://mydomain.com/mymodel.glb*
     */
    constructor(
        context: Context,
        modelGlbFileLocation: String,
        coroutineScope: LifecycleCoroutineScope? = null,
        onModelLoaded: ((instance: RenderableInstance) -> Unit)? = null,
        onError: ((error: Exception) -> Unit)? = null,
        parent: NodeParent? = null,
        position: Vector3 = defaultPosition,
        rotationQuaternion: Quaternion = defaultRotation,
        scales: Vector3 = defaultScales,
    ) : this(position, rotationQuaternion, scales, parent) {
        loadModel(context, modelGlbFileLocation, coroutineScope, onModelLoaded, onError)
    }

    constructor(node: ModelNode) : this(
        position = node.position,
        rotationQuaternion = node.rotationQuaternion,
        scales = node.scales
    ) {
        setRenderable(node.renderable)
    }

    override fun onFrame(frameTime: FrameTime) {
        if (isRendered) {
            // TODO : Remove the renderable.id thing when Renderable is kotlined
            // Update state when the renderable has changed.
            renderable?.let { renderable ->
                if (renderable.id.checkChanged(renderableId)) {
                    onRenderableChanged()
                }
            }
        }
        super.onFrame(frameTime)
    }

    open fun onModelLoaded(renderableInstance: RenderableInstance) {
        onModelLoaded?.invoke(renderableInstance)
    }

    open fun onError(exception: Exception) {
        onError?.invoke(exception)
    }

    /**
     * ### The transformation of the [Node] has changed
     *
     * If node A's position is changed, then that will trigger [onTransformChanged] to be
     * called for all of it's descendants.
     */
    open fun onRenderableChanged() {
        // Refresh the collider to ensure it is using the correct collision shape now
        // that the renderable has changed.
        onTransformChanged()

        collisionShape = renderable?.collisionShape
        // TODO : Clean when Renderable is kotlined
        renderableId = renderable?.id?.get() ?: ChangeId.EMPTY_ID
    }

    /**
     * @param glbFileLocation the glb file location:
     * - A relative asset file location *models/mymodel.glb*
     * - An android resource from the res folder *context.getResourceUri(R.raw.mymodel)*
     * - A File path *Uri.fromFile(myModelFile).path*
     * - An http or https url *https://mydomain.com/mymodel.glb*
     */
    fun loadModel(
        context: Context,
        glbFileLocation: String,
        coroutineScope: LifecycleCoroutineScope? = null,
        onLoaded: ((instance: RenderableInstance) -> Unit)? = null,
        onError: ((error: Exception) -> Unit)? = null
    ) {
        if (coroutineScope != null) {
            coroutineScope.launchWhenCreated {
                try {
                    val instance = setRenderable(GlbLoader.loadModel(context, glbFileLocation))
                    onLoaded?.invoke(instance!!)
                    onModelLoaded(instance!!)
                } catch (error: Exception) {
                    onError?.invoke(error)
                    onError(error)
                }
            }
        } else {
            doOnAttachedToScene { scene ->
                loadModel(context, glbFileLocation, scene.lifecycleScope, onLoaded, onError)
            }
        }
    }

    open fun setRenderable(renderable: Renderable?): RenderableInstance? {
        renderableInstance = renderable?.createInstance(this)
        return renderableInstance
    }

    /** ### Detach and destroy the node */
    override fun destroy() {
        super.destroy()
        renderableInstance?.destroy()
    }

    override fun clone() = copy(ModelNode())

    fun copy(toNode: ModelNode = ModelNode()): ModelNode = toNode.apply {
        super.copy(toNode)
        setRenderable(this@ModelNode.renderable)
    }
}
