package ru.ha_inc.bedcook.order

import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityOrderBinding
import ru.ha_inc.bedcook.game.FullscreenActivity
import ru.ha_inc.bedcook.models.Complexity
import ru.ha_inc.bedcook.models.Order

class OrderActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ORDER = "EXTRA_ORDER"
    }

    private val binding by viewBinding(ActivityOrderBinding::bind)
    private val viewModel by viewModels<OrderViewModel>()

    private var soundPool: SoundPool? = null
    private val soundId = 1
    private val soundId2 = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val order = intent.getSerializableExtra(EXTRA_ORDER) as? Order
        order?.let { showOrder(it) }

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool?.load(baseContext, R.raw.btn, 1)
        soundPool?.load(baseContext, R.raw.btn_next, 2)

        binding.btnBack.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            finish()
        }
        binding.btnNext.setOnClickListener {
            soundPool?.play(soundId2, 1F, 1F, 0, 0, 1F)
            startActivity(Intent(this, FullscreenActivity::class.java).apply {
                putExtra(EXTRA_ORDER, order)
            })
        }


    }

    private fun showOrder(order: Order) {
        binding.ivTask.background.level = order.complexity.level
        binding.tvComplexity.setTextColor(
            getColor(
                when (order.complexity) {
                    Complexity.EASY -> R.color.easy_task
                    Complexity.NORMAL -> R.color.normal_task
                    Complexity.HARD -> R.color.hard_task
                }
            )
        )
        binding.tvMoney.text = "+ ${order.salary}"
        binding.tvScore.text = "+ ${order.scoreBonus}"

        binding.tvNameCake.text = order.cake.name
        binding.ivTask.setImageResource(order.cake.drawable)
    }
}