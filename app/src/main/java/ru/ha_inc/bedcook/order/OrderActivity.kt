package ru.ha_inc.bedcook.order

import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityOrderBinding
import ru.ha_inc.bedcook.databinding.ActivityStartBinding
import ru.ha_inc.bedcook.finish.FinishActivity
import ru.ha_inc.bedcook.game.FullscreenActivity
import ru.ha_inc.bedcook.map.MapActivity
import ru.ha_inc.bedcook.start.StartActivity
import ru.ha_inc.bedcook.start.StartViewModel

class OrderActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityOrderBinding::bind)
    private val viewModel by viewModels<OrderViewModel>()

    private var soundPool: SoundPool? = null
    private val soundId = 1
    private val soundId2 = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool?.load(baseContext, R.raw.btn, 1)
        soundPool?.load(baseContext, R.raw.btn_next, 2)

        viewModel.complexity.observe(this) {
            binding.ivTask.background.level = it
            binding.tvComplexity.setTextColor(getColor(
                when(it){
                    0 -> R.color.easy_task
                    1 -> R.color.normal_task
                    2 -> R.color.hard_task
                    else -> R.color.easy_task
            }))
            binding.tvMoney.text = "+ ${viewModel.salary.value}"
            binding.tvPoint.text = "+ ${viewModel.pointBonus.value}"
        }
        viewModel.typeCake.observe(this) {
            binding.tvNameCake.text = it.name
            binding.ivTask.setImageResource(it.drawable)
        }
        binding.btnBack.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            finish()
        }
        binding.btnNext.setOnClickListener {
            soundPool?.play(soundId2, 1F, 1F, 0, 0, 1F)
            startActivity(Intent(this, FullscreenActivity::class.java))
        }


    }
}