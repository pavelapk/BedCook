package ru.ha_inc.bedcook.profile

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

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
           startActivity(Intent(this, StartActivity::class.java))
        }

        binding.btnNextDay.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        binding.btnShop.setOnClickListener {
            viewModelProfile.addPoint()
        }

    }
}