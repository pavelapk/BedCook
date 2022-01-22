package ru.ha_inc.bedcook.start

import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import io.github.sceneview.utils.setFullScreen
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityStartBinding

import ru.ha_inc.bedcook.profile.ProfileActivity
import ru.ha_inc.bedcook.rules.RulesActivity
import ru.ha_inc.bedcook.utils.MusicService


class StartActivity : AppCompatActivity(R.layout.activity_start) {

    private val binding by viewBinding(ActivityStartBinding::bind)
    private val viewModel by viewModels<StartViewModel>()

    private var soundPool: SoundPool? = null
    private val soundId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen(
            fullScreen = true, hideSystemBars = true,
            fitsSystemWindows = false, rootView = binding.root
        )

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool?.load(baseContext, R.raw.btn, 1)


        viewModel.gameSound.observe(this) {
            binding.btnSound.isChecked = it
            if (it) {
                startService(Intent(this, MusicService::class.java))
            } else {
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
            startActivity(Intent(this, RulesActivity::class.java))
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