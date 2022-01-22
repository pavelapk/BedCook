package ru.ha_inc.bedcook.game

import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.sceneform.math.Vector3
import io.github.sceneview.SceneView
import io.github.sceneview.ar.arcore.depthEnabled
import io.github.sceneview.ar.arcore.instantPlacementEnabled
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.utils.setFullScreen
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityFullscreenBinding
import ru.ha_inc.bedcook.finish.FinishActivity
import ru.ha_inc.bedcook.models.Order
import ru.ha_inc.bedcook.order.OrderActivity
import java.io.IOException


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityFullscreenBinding::bind)
    private val viewModel by viewModels<GameViewModel>()
    private var modelNode: ArNode? = null

    private var soundPool: SoundPool? = null
    private val soundId = 1
    private val soundId2 = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        setFullScreen(
            fullScreen = true, hideSystemBars = false,
            fitsSystemWindows = false, rootView = binding.root
        )

        val order = intent.getSerializableExtra(OrderActivity.EXTRA_ORDER) as? Order
        order?.let {
            binding.ivTaskCake.setImageResource(it.cake.drawable)
        }

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool?.load(baseContext, R.raw.btn_order, 1)
        soundPool?.load(baseContext, R.raw.star, 2)

        binding.arSceneView.configureSession {
            it.depthEnabled = false
            it.instantPlacementEnabled = false
            it.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            it.cloudAnchorMode = Config.CloudAnchorMode.DISABLED
            it.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
            it.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            it.focusMode =
                Config.FocusMode.AUTO // bad work when updateMode == LATEST_CAMERA_IMAGE
        }

        binding.arSceneView.onARCoreException = {
            it.printStackTrace()
        }

        binding.btnDelete.setOnClickListener {
            binding.arSceneView.nodeGestureRecognizer.selectedNode?.apply {
                binding.arSceneView.removeChild(this)
                destroy()
            }

        }

        val adapter = SelectableObjectAdapter(viewModel.objects) {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            binding.arSceneView.setOnTapArPlaneGlbModel(it.modelPath,
                onAdded = { arNode, _ ->
                    resetSceneViewOnTouch()
                    arNode.onSelected.add {
                        resetSceneViewOnTouch()
                    }
                },
                onError = {
                    it.printStackTrace()
                    resetSceneViewOnTouch()
                }
            )
        }

        binding.recyclerSelectObject.adapter = adapter
        binding.sceneView.camera.apply {
            position = Vector3(0f, 0.1f, 0.2f)
            rotation = Vector3(-20f, 0f, 0f)
        }
        binding.btnTaskDetails.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)

            if (binding.sendOrder.visibility != View.VISIBLE) {
                binding.sendOrder.visibility = View.VISIBLE
                viewModel.renderSceneView(binding.arSceneView, binding.sceneView)
            } else {
                viewModel.clearSceneView(binding.sceneView)
                binding.sendOrder.visibility = View.GONE
            }
        }

        binding.btnFinish.setOnClickListener { _ ->
            soundPool?.play(soundId2, 1F, 1F, 0, 0, 1F)
            order?.let { it ->
                viewModel.finishTask(it, binding.sceneView) { orderResult ->
                    startActivity(Intent(this, FinishActivity::class.java).apply {
                        putExtra(FinishActivity.EXTRA_ORDER_RESULT, orderResult)
                    })
                }
            }
        }
        binding.btnCancel.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            binding.sendOrder.visibility = View.GONE
        }
    }

    private fun resetSceneViewOnTouch() {
        binding.arSceneView.onTouchAr = { _, _ ->
            binding.arSceneView.nodeGestureRecognizer.selectNode(null)
        }
    }


    private fun screenshotSceneView(sceneView: SceneView) {
        Handler(mainLooper).post {
            val bitmap = Bitmap.createBitmap(
                512, 512,
                Bitmap.Config.ARGB_8888
            )

            // Create a handler thread to offload the processing of the image.
            val handlerThread = HandlerThread("PixelCopier")
            handlerThread.start()
            // Make the request to copy.
            PixelCopy.request(sceneView, bitmap, { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    try {
                        runOnUiThread {
                            //binding.ivResult.setImageBitmap(bitmap)
                        }
                    } catch (e: IOException) {
                        Log.w("DADAYA", "pixel copy ", e)
                        return@request
                    }
                } else {
                    Log.w("DADAYA", "unsuccess: $copyResult")
                }
                handlerThread.quitSafely()
            }, Handler(handlerThread.looper))
        }
    }

    private fun anchorOrMove(anchor: Anchor) {
        if (modelNode == null) {
//            isLoading = true
            binding.arSceneView.planeRenderer.isEnabled = false
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
                binding.arSceneView.addChild(it)
            }
        } else {
            modelNode?.anchor = anchor
        }
    }

}
