package ru.ha_inc.bedcook

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import io.github.sceneview.utils.setFullScreen

open class BaseActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setFullScreen(
//            fullScreen = true, hideSystemBars = true,
//            fitsSystemWindows = false, rootView = binding.root
//        )
    }
}