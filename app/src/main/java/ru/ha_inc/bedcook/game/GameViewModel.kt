package ru.ha_inc.bedcook.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.ar.sceneform.math.Vector3
import io.github.sceneview.SceneView
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.utils.Math.average

class GameViewModel : ViewModel() {

    val objects = listOf(
        SelectableObject("Корж", R.drawable.sceneview_logo, "cake_layer.glb"),
        SelectableObject("Вишня", R.drawable.sceneview_logo, "cherry.glb"),
        SelectableObject("Крем", R.drawable.sceneview_logo, "cake_layer_cream.glb"),
        SelectableObject("Куб", R.drawable.sceneview_logo, "cube.glb"),
        SelectableObject("Торт", R.drawable.sceneview_logo, "cake.glb"),
    )

    fun renderSceneView(arSceneView: ArSceneView, sceneView: SceneView) {
        viewModelScope.launch {
            arSceneView.children.filterIsInstance<ArNode>().let {
                val avgPos = calcNodesAvgPos(it)
                it.forEach { node ->
                    sceneView.addChild(createModelNodeFromAr(node).apply {
                        position = Vector3.subtract(node.worldPosition, avgPos)
                    })
                }
            }

        }
    }

    fun clearSceneView(sceneView: SceneView) {
        sceneView.callOnHierarchy {
            if (it is ModelNode) {
                it.destroy()
            }
        }
    }

    private fun createModelNodeFromAr(arNode: ArNode): ModelNode {
        val resultNode = ModelNode(arNode)
        arNode.children.filterIsInstance<ArNode>().forEach {
            resultNode.addChild(createModelNodeFromAr(it))
        }
        return resultNode
    }

    private fun calcNodesAvgPos(list: List<ArNode>): Vector3 =
        list.map { it.worldPosition }.average()

}