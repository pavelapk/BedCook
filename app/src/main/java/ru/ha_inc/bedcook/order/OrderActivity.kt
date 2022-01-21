package ru.ha_inc.bedcook.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityOrderBinding
import ru.ha_inc.bedcook.databinding.ActivityStartBinding

class OrderActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityOrderBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)


        binding.ivTask.background.level = 2

    }
}