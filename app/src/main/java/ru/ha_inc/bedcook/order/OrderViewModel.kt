package ru.ha_inc.bedcook.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ha_inc.bedcook.models.Cake
import ru.ha_inc.bedcook.models.CakeDataSource
import ru.ha_inc.bedcook.utils.MutableLiveData2

class OrderViewModel : ViewModel() {

    private val _salary = MutableLiveData2<Int>(0)
    val salary: LiveData<Int> get() = _salary

    private val _pointBonus = MutableLiveData2<Int>(0)
    val pointBonus: LiveData<Int> get() = _pointBonus

    private val _complexity = MutableLiveData2<Int>(0)
    val complexity: LiveData<Int> get() = _complexity

    private val _typeCake = MutableLiveData<Cake>()
    val typeCake: LiveData<Cake> get() = _typeCake



    init{
        _complexity.value = 2
        _typeCake.value = CakeDataSource.cakes[0]
        when(_complexity.value){
            0 -> initOrder(5000, 100)
            1 -> initOrder(10000, 150)
            2 -> initOrder(15000, 200)
            else -> initOrder(5000, 100)
        }
    }

    private fun initOrder(salary: Int, point: Int){
        _salary.value = salary
        _pointBonus.value = point
    }
}