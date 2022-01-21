package ru.ha_inc.bedcook

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import ru.ha_inc.bedcook.models.ProfileRepository
import ru.ha_inc.bedcook.utils.MusicService


internal class BedCookApplication : Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (ProfileRepository(this).musicEnabled) {
            startService(Intent(this, MusicService::class.java))
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        stopService(Intent(this, MusicService::class.java))
    }
}