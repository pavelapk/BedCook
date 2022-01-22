package ru.ha_inc.bedcook.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast

import android.widget.VideoView
import java.lang.Exception


object VideoViewUtils {

    val RAW_VIDEO_SAMPLE = "video"
    val LOG_TAG = "AndroidVideoView"


    // Play a video in directory RAW.
    // Video name = "myvideo.mp4" ==> resName = "myvideo".
    fun playRawVideo(context: Context, videoView: VideoView, resName: String) {
        try {
            // ID of video file.
            val id: Int = getRawResIdByName(context, resName)
            val uri: Uri =
                Uri.parse("android.resource://" + context.packageName.toString() + "/" + id)
            Log.i(LOG_TAG, "Video URI: $uri")
            videoView.setVideoURI(uri)
            videoView.requestFocus()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error Play Raw Video: " + e.message)
            Toast.makeText(context, "Error Play Raw Video: " + e.message, Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // Find ID corresponding to the name of the resource (in the directory RAW).
    fun getRawResIdByName(context: Context, resName: String): Int {
        val pkgName = context.packageName
        // Return 0 if not found.
        val resID = context.resources.getIdentifier(resName, "raw", pkgName)
        Log.i(LOG_TAG, "Res Name: $resName==> Res ID = $resID")
        return resID
    }
}