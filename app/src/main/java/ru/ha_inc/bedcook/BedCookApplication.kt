package ru.ha_inc.bedcook

import androidx.lifecycle.Lifecycle

import androidx.lifecycle.OnLifecycleEvent

import androidx.lifecycle.LifecycleObserver

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ProcessLifecycleOwner
import ru.ha_inc.bedcook.utils.MusicService


internal class BedCookApplication : Application(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        stopService(Intent(this, MusicService::class.java))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        startService(Intent(this, MusicService::class.java))
    }
}