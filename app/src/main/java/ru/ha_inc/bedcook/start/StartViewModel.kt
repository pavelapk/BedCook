package ru.ha_inc.bedcook.start

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ha_inc.bedcook.R

class StartViewModel : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    init {
        viewModelScope.launch {
            _username.value = "Мелкая"
        }
    }

    fun toggleSound(isPlay: Boolean) {
        Log.d("DADAYA", "toggleSound: $isPlay")
    }

    fun onPlayClick(username: String, onSuccess: () -> Unit, onFailure: (Int) -> Unit) {
        if (username.isNotBlank()) {
            onSuccess.invoke()
        } else {
            onFailure.invoke(R.string.username_blank_error)
        }
    }
}