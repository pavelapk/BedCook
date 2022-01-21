package ru.ha_inc.bedcook.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.databinding.ActivityFullscreenBinding
import ru.ha_inc.bedcook.databinding.ActivityStartBinding
import ru.ha_inc.bedcook.game.FullscreenActivity
import ru.ha_inc.bedcook.profile.ProfileActivity
import ru.ha_inc.bedcook.utils.MusicService
import android.media.SoundPool
import androidx.core.content.PackageManagerCompat.LOG_TAG

import android.media.AudioManager
import androidx.core.content.PackageManagerCompat
import ru.ha_inc.bedcook.R


class StartActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityStartBinding::bind)
    private val viewModel by viewModels<StartViewModel>()

    private var soundPool: SoundPool? = null
    private val soundId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool?.load(baseContext, R.raw.btn, 1)


        viewModel.gameSound.observe(this) {
            if(it){
                startService(Intent(this, MusicService::class.java))
            }
            else{
                stopService(Intent(this, MusicService::class.java))
            }
        }


        binding.btnSound.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            viewModel.toggleSound(binding.btnSound.isChecked)
        }

        binding.btnRules.setOnClickListener {
           // startActivity(Intent(this, FullscreenActivity::class.java))
            viewModel.runVideoRules()
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
        }

        viewModel.username.observe(this) {
            binding.etUsername.setText(it)
        }

        binding.btnPlay.setOnClickListener {
            viewModel.onPlayClick(binding.etUsername.text.toString(),
                onSuccess = {
                    soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
                    startActivity(Intent(this, ProfileActivity::class.java))
                },
                onFailure = {
                    binding.etUsername.error = getString(it)
                }
            )
        }
    }

}