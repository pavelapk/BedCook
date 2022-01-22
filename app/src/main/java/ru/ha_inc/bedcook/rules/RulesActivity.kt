package ru.ha_inc.bedcook.rules

import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityRulesBinding
import ru.ha_inc.bedcook.utils.VideoViewUtils




class RulesActivity : AppCompatActivity(R.layout.activity_rules) {

    private val binding by viewBinding(ActivityRulesBinding::bind)
    private var videoView: VideoView? = null
    private var position = 0
    private var mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the media controller buttons
        if (mediaController == null) {
            mediaController = MediaController(this)

            // Set the videoView that acts as the anchor for the MediaController.
            mediaController?.setAnchorView(videoView)

            // Set MediaController for VideoView
            videoView?.setMediaController(mediaController)
        }


        // When the video file ready for playback.
        videoView?.setOnPreparedListener { mediaPlayer ->
            videoView?.seekTo(position)
            if (position == 0) {
                videoView?.start()
            }

            // When video Screen change size.
            mediaPlayer.setOnVideoSizeChangedListener { mp, width, height -> // Re-Set the videoView that acts as the anchor for the MediaController
                mediaController?.setAnchorView(videoView)
            }
        }

        binding.videoView.setOnClickListener {
            val resName: String = VideoViewUtils.RAW_VIDEO_SAMPLE
            videoView?.let { it1 -> VideoViewUtils.playRawVideo(this, it1, resName) }
        }

    }



    // When you change direction of phone, this method will be called.
    // It store the state of video (Current position)
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        // Store current position.
        videoView?.let { savedInstanceState.putInt("CurrentPosition", it.currentPosition) }
        videoView?.pause()
    }


    // After rotating the phone. This method is called.
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Get saved position.
        position = savedInstanceState.getInt("CurrentPosition")
        videoView?.seekTo(position)
    }


}