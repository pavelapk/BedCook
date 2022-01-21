package ru.ha_inc.bedcook.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ha_inc.bedcook.utils.MutableLiveData2

class ProfileViewModel : ViewModel() {

    private val moneyBonusLevelUp = 10000

    private val _money = MutableLiveData2<Int>(0)
    val money: LiveData<Int> get() = _money

    private val _point = MutableLiveData2<Int>(0)
    val point: LiveData<Int> get() = _point

    private val _level = MutableLiveData2<Int>(1)
    val level: LiveData<Int> get() = _level

    private val _skin = MutableLiveData2<Int>(1)
    val skin: LiveData<Int> get() = _skin

    init {
        _money.value = 0
        _level.value = 1
        _point.value = 0
        _skin.value = 1
    }


    private fun levelUp(){
        while(_point.value >= _level.value * 100){
            _point.value -= _level.value * 100
            _level.value += 1
            _money.value += moneyBonusLevelUp
        }
    }

    fun addPoint(){
        _point.value += 300
        levelUp()
    }

    fun getReward(money: Int, point: Int){
        _money.value += money
        _point.value += point
    }

}