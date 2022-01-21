package ru.ha_inc.bedcook.finish

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.ha_inc.bedcook.utils.MutableLiveData2

class FinishViewModel : ViewModel() {

    private val _salaryResult = MutableLiveData2<Int>(100)
    val salaryResult: LiveData<Int> get() = _salaryResult

    private val _pointBonusResult = MutableLiveData2<Int>(10)
    val pointBonusResult: LiveData<Int> get() = _pointBonusResult

    private val _stars = MutableLiveData2<Int>(0)
    val stars: LiveData<Int> get() = _stars

    init {
        _stars.value = 1
    }

    fun initResult(salary: Int, point: Int, result1: Int, result2: Int, result3: Int){
        val result = result1 + result2 + result3
        if(result >= 250){
            _salaryResult.value = salary
            _pointBonusResult.value = point
            _stars.value = 3
        }
        else{
           if(result >=150){
               _salaryResult.value = (salary * 0.8).toInt()
               _pointBonusResult.value = point
               _stars.value = 2
           }
            else{
               _salaryResult.value = (salary * 0.5).toInt()
               _pointBonusResult.value = point
               _stars.value = 1
           }
        }
    }
}