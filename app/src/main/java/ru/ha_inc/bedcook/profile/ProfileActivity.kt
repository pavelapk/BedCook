package ru.ha_inc.bedcook.profile

import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityProfileBinding
import ru.ha_inc.bedcook.databinding.ActivityStartBinding
import ru.ha_inc.bedcook.map.MapActivity
import ru.ha_inc.bedcook.order.OrderActivity
import ru.ha_inc.bedcook.start.StartActivity
import ru.ha_inc.bedcook.start.StartViewModel

class ProfileActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityProfileBinding::bind)
    private val viewModelStart by viewModels<StartViewModel>()
    private val viewModelProfile by viewModels<ProfileViewModel>()

    private var soundPool: SoundPool? = null
    private val soundId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool?.load(baseContext, R.raw.btn, 1)

        viewModelProfile.level.observe(this) {
            binding.tvLevel.text = it.toString()
        }
        viewModelProfile.point.observe(this) {
            binding.tvPointLevel.text = "$it/${100 * (viewModelProfile.level.value?:0)}"
            binding.pbLevel.progress = it
            binding.pbLevel.max = 100 * (viewModelProfile.level.value?:0)
        }
        viewModelProfile.money.observe(this) {
            binding.tvMoney.text = it.toString()
        }

        binding.btnExit.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            startActivity(Intent(this, StartActivity::class.java))
        }

        binding.btnNextDay.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            startActivity(Intent(this, MapActivity::class.java))
        }

        binding.btnShop.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            viewModelProfile.addPoint()
        }

    }
}