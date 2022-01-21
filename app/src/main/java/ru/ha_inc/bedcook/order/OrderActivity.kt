package ru.ha_inc.bedcook.order

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityOrderBinding
import ru.ha_inc.bedcook.finish.FinishActivity
import ru.ha_inc.bedcook.map.MapActivity

class OrderActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityOrderBinding::bind)
    private val viewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        viewModel.complexity.observe(this) {
            binding.ivTask.background.level = it
            binding.tvComplexity.setTextColor(
                getColor(
                    when (it) {
                        0 -> R.color.easy_task
                        1 -> R.color.normal_task
                        2 -> R.color.hard_task
                        else -> R.color.easy_task
                    }
                )
            )
            binding.tvMoney.text = "+ ${viewModel.salary.value}"
            binding.tvPoint.text = "+ ${viewModel.pointBonus.value}"
        }
        viewModel.typeCake.observe(this) {
            binding.tvNameCake.text = it.name
            binding.ivTask.setImageResource(it.drawable)
        }
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, FinishActivity::class.java))
        }


    }
}