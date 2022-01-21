package ru.ha_inc.bedcook.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.ha_inc.bedcook.models.Profile
import ru.ha_inc.bedcook.models.ProfileRepository
import ru.ha_inc.bedcook.utils.MutableLiveData2


class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val moneyBonusLevelUp = 10000

    private val _money = MutableLiveData2<Int>(0)
    val money: LiveData<Int> get() = _money

    private val _score = MutableLiveData2<Int>(0)
    val score: LiveData<Int> get() = _score

    private val _level = MutableLiveData2<Int>(1)
    val level: LiveData<Int> get() = _level

    private val _skin = MutableLiveData2<Int>(1)
    val skin: LiveData<Int> get() = _skin
    private val profileRepository = ProfileRepository(app)

    init {
        viewModelScope.launch {
            profileRepository.getProfile().collectLatest { profile ->
                profile.level?.let { _level.value = it }
                profile.score?.let { _score.value = it }
                profile.money?.let { _money.value = it }
                _skin.value = 1
            }
        }

    }

    private fun levelUp() {
        while (_score.value >= _level.value * 100) {
            _score.value -= _level.value * 100
            _level.value += 1
            _money.value += moneyBonusLevelUp
        }
    }

    fun addPoint() {
        viewModelScope.launch {
            _score.value += 300
            levelUp()
            profileRepository.updateProfile(
                Profile(
                    level = _level.value,
                    score = _score.value,
                    money = _money.value
                )
            )
        }
    }

    fun getReward(money: Int, point: Int) {
        _money.value += money
        _score.value += point
    }

}