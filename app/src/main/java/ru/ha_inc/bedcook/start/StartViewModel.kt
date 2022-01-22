package ru.ha_inc.bedcook.start

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.models.ProfileRepository

class StartViewModel(app: Application) : AndroidViewModel(app) {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _gameSound = MutableLiveData<Boolean>()
    val gameSound: LiveData<Boolean> get() = _gameSound

    private val profileRepository = ProfileRepository(app)

    init {
        viewModelScope.launch {
            _gameSound.value = profileRepository.musicEnabled
            profileRepository.getProfile().collectLatest { profile ->
                profile.username?.let { _username.value = it }
            }
        }
    }

    fun toggleSound(isPlay: Boolean) {
        _gameSound.value = isPlay
        profileRepository.musicEnabled = isPlay
    }

    fun onPlayClick(username: String, onSuccess: () -> Unit, onFailure: (Int) -> Unit) {
        viewModelScope.launch {
            if (username.isNotBlank()) {
                _username.value = username
                if (profileRepository.isProfileStored()) {
                    profileRepository.updateUsername(username)
                } else {
                    profileRepository.createUser(username)
                }
                onSuccess.invoke()
            } else {
                onFailure.invoke(R.string.username_blank_error)
            }
        }
    }
}