package ru.ha_inc.bedcook.map

import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityMapBinding
import ru.ha_inc.bedcook.databinding.ActivityProfileBinding
import ru.ha_inc.bedcook.order.OrderActivity

class MapActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMapBinding::bind)
    private var soundPool: SoundPool? = null
    private val soundId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool?.load(baseContext, R.raw.btn, 1)

        binding.order1.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            startActivity(Intent(this, OrderActivity::class.java))
        }
        binding.order2.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            startActivity(Intent(this, OrderActivity::class.java))
        }
        binding.order3.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            startActivity(Intent(this, OrderActivity::class.java))
        }
    }
}