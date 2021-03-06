package ru.ha_inc.bedcook.game

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import io.github.sceneview.ar.arcore.depthEnabled
import io.github.sceneview.ar.arcore.instantPlacementEnabled
import io.github.sceneview.ar.node.ArNode
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityFullscreenBinding

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityFullscreenBinding::bind)
    private var modelNode: ArNode? = null

    private val objects = listOf(
        SelectableObject("Торт", R.drawable.sceneview_logo),
        SelectableObject("Торт1", R.drawable.sceneview_logo),
        SelectableObject("Торт2", R.drawable.sceneview_logo),
        SelectableObject("Торт3", R.drawable.sceneview_logo),
    )

    private val adapter =
        SelectableObjectAdapter(objects) {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        binding.sceneView.configureSession {
            it.depthEnabled = false
            it.instantPlacementEnabled = false
            it.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            it.cloudAnchorMode = Config.CloudAnchorMode.DISABLED
            it.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
            it.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            it.focusMode =
                Config.FocusMode.AUTO // don't work when updateMode == LATEST_CAMERA_IMAGE
        }
//        binding.sceneView.planeRenderer.planeRendererMode =
//            PlaneRenderer.PlaneRendererMode.RENDER_TOP_MOST

        binding.sceneView.onARCoreException = {
            it.printStackTrace()
        }

        binding.sceneView.onTouchAr = { hitResult, _ ->
            Log.d("DADAYA", hitResult.distance.toString())
            Log.d("DADAYA", hitResult.hitPose.toString())
            anchorOrMove(hitResult.createAnchor())
        }

        binding.dummyButton.setOnClickListener {
            modelNode?.apply {
                binding.sceneView.removeChild(this)
                destroy()
            }
            binding.sceneView.planeRenderer.isEnabled = true
            modelNode = null
        }

        binding.recyclerSelectObject.adapter = adapter
    }

    private fun anchorOrMove(anchor: Anchor) {
        if (modelNode == null) {
//            isLoading = true
            binding.sceneView.planeRenderer.isEnabled = false
            modelNode = ArNode(
                context = this,
                coroutineScope = lifecycleScope,
                anchor = anchor,
                modelGlbFileLocation = "cake.glb",
                onModelLoaded = {
//                    actionButton.text = getString(R.string.move_object)
//                    actionButton.icon = resources.getDrawable(R.drawable.ic_target)
//                    isLoading = false
                }).also {
                it.positionY = 0f
                it.scale = 1f
                binding.sceneView.addChild(it)
            }
        } else {
            modelNode?.anchor = anchor
        }
    }

}