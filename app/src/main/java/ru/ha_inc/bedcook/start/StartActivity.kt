package ru.ha_inc.bedcook.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityFullscreenBinding
import ru.ha_inc.bedcook.databinding.ActivityStartBinding
import ru.ha_inc.bedcook.game.FullscreenActivity
import ru.ha_inc.bedcook.profile.ProfileActivity

class StartActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityStartBinding::bind)
    private val viewModel by viewModels<StartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        binding.btnSound.setOnClickListener {
            viewModel.toggleSound(binding.btnSound.isChecked)
        }

        binding.btnRules.setOnClickListener {
            startActivity(Intent(this, FullscreenActivity::class.java))
        }

        viewModel.username.observe(this) {
            binding.etUsername.setText(it)
        }

        binding.btnPlay.setOnClickListener {
            viewModel.onPlayClick(binding.etUsername.text.toString(),
                onSuccess = {
                    startActivity(Intent(this, ProfileActivity::class.java))
                },
                onFailure = {
                    binding.etUsername.error = getString(it)
                }
            )
        }
    }
}