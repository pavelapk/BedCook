package ru.ha_inc.bedcook.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.ha_inc.bedcook.utils.MutableLiveData2

class OrderViewModel : ViewModel() {

    private val _salary = MutableLiveData2<Int>(0)
    val salary: LiveData<Int> get() = _salary

    private val _pointBonus = MutableLiveData2<Int>(0)
    val pointBonus: LiveData<Int> get() = _pointBonus

    private val _complexity = MutableLiveData2<Int>(0)
    val complexity: LiveData<Int> get() = _complexity



}