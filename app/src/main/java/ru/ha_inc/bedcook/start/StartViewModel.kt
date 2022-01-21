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

    private val _gameSound = MutableLiveData<Boolean>()
    val gameSound: LiveData<Boolean> get() = _gameSound

    init {
        viewModelScope.launch {
            _username.value = "Мелкая"
        }
    }

    fun toggleSound(isPlay: Boolean) {
        Log.d("DADAYA", "toggleSound: $isPlay")
        _gameSound.value = isPlay
        if(_gameSound.value == true){
            //вкл звук
        }
        else{
            //выкл звук
        }
    }

    fun runVideoRules(){
        //вкл видео
    }

    fun onPlayClick(username: String, onSuccess: () -> Unit, onFailure: (Int) -> Unit) {
        if (username.isNotBlank()) {
            _username.value = username
            onSuccess.invoke()
        } else {
            onFailure.invoke(R.string.username_blank_error)
        }
    }
}