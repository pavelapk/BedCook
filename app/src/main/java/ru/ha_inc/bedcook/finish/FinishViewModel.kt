package ru.ha_inc.bedcook.finish

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ha_inc.bedcook.models.OrderResult
import ru.ha_inc.bedcook.models.Profile
import ru.ha_inc.bedcook.models.ProfileRepository
import ru.ha_inc.bedcook.utils.MutableLiveData2

class FinishViewModel(app: Application) : AndroidViewModel(app) {

    private val profileRepository = ProfileRepository(app)


    fun updateProfile(orderResult: OrderResult) {
        viewModelScope.launch {
            profileRepository.gameResult(orderResult.order.salary)
        }
    }

//    fun initResult(salary: Int, point: Int, result1: Int, result2: Int, result3: Int) {
//        val result = result1 + result2 + result3
//        if (result >= 250) {
//            _salaryResult.value = salary
//            _pointBonusResult.value = point
//            _stars.value = 3
//        } else {
//            if (result >= 150) {
//                _salaryResult.value = (salary * 0.8).toInt()
//                _pointBonusResult.value = point
//                _stars.value = 2
//            } else {
//                _salaryResult.value = (salary * 0.5).toInt()
//                _pointBonusResult.value = point
//                _stars.value = 1
//            }
//        }
//    }
}