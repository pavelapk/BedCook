package ru.ha_inc.bedcook.finish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityFinishBinding
import ru.ha_inc.bedcook.databinding.ActivityProfileBinding
import ru.ha_inc.bedcook.map.MapActivity
import ru.ha_inc.bedcook.order.OrderViewModel
import ru.ha_inc.bedcook.profile.ProfileActivity
import ru.ha_inc.bedcook.profile.ProfileViewModel
import ru.ha_inc.bedcook.start.StartViewModel

class FinishActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityFinishBinding::bind)
    private val viewModelFinish by viewModels<FinishViewModel>()
    private val viewModelOrder by viewModels<OrderViewModel>()
    private val viewModelProfile by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        viewModelFinish.stars.observe(this) { countStars ->
            binding.tvResult1.text = "100%"
            binding.tvResult2.text = "90%"
            binding.tvResult3.text = "98%"

            val stars = arrayOf(binding.ivStar1, binding.ivStar2, binding.ivStar3)
            stars.forEach {
                it.visibility = View.INVISIBLE
            }
            for (i in 0 until countStars) {
                stars[i].visibility = View.VISIBLE
            }
        }


        binding.btnGoBack.setOnClickListener {
            viewModelProfile.getReward(
                viewModelFinish.salaryResult.value ?: 0,
                viewModelFinish.pointBonusResult.value ?: 0
            )
            startActivity(Intent(this, ProfileActivity::class.java))
        }


    }
}