package ru.ha_inc.bedcook.finish

import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityFinishBinding
import ru.ha_inc.bedcook.databinding.ActivityProfileBinding
import ru.ha_inc.bedcook.map.MapActivity
import ru.ha_inc.bedcook.models.Order
import ru.ha_inc.bedcook.models.OrderResult
import ru.ha_inc.bedcook.order.OrderActivity
import ru.ha_inc.bedcook.order.OrderViewModel
import ru.ha_inc.bedcook.profile.ProfileActivity
import ru.ha_inc.bedcook.profile.ProfileViewModel
import ru.ha_inc.bedcook.start.StartViewModel

class FinishActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ORDER_RESULT = "EXTRA_ORDER_RESULT"
    }

    private val binding by viewBinding(ActivityFinishBinding::bind)
    private val viewModelFinish by viewModels<FinishViewModel>()

    private var soundPool: SoundPool? = null
    private val soundId = 1
    private val soundId2 = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool?.load(baseContext, R.raw.btn, 1)
        soundPool?.load(baseContext, R.raw.launch, 2)

        val orderResult = intent.getSerializableExtra(OrderActivity.EXTRA_ORDER) as? OrderResult

        orderResult?.let {
            viewModelFinish.updateProfile(it)
            showResults(it)
        }

        binding.btnGoBack.setOnClickListener {
            soundPool?.play(soundId2, 1F, 1F, 0, 0, 1F)
            startActivity(Intent(this, ProfileActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }


    }

    private fun showResults(orderResult: OrderResult) {
        binding.tvResult1.text = "${orderResult.result1}%"
        binding.tvResult2.text = "${orderResult.result2}%"
        binding.tvResult3.text = "${orderResult.result3}%"


        val stars = arrayOf(binding.ivStar1, binding.ivStar2, binding.ivStar3)
        stars.forEach {
            it.visibility = View.INVISIBLE
        }
        for (i in 0..orderResult.order.complexity.level) {
            stars[i].visibility = View.VISIBLE
        }
    }
}