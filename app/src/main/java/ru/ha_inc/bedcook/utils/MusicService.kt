package ru.ha_inc.bedcook.utils

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.widget.Toast
import ru.ha_inc.bedcook.R

class MusicService : Service() {
    var player: MediaPlayer? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show()
        player = MediaPlayer.create(this, R.raw.bedcook_background)
        player?.isLooping = true // зацикливаем
    }

    override fun onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show()
        player?.stop()
    }

    override fun onStart(intent: Intent, startid: Int) {
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show()
        player?.start()
    }

    companion object {
        private const val TAG = "MyService"
    }
}