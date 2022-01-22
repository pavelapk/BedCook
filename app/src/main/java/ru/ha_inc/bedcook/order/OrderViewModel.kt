package ru.ha_inc.bedcook.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ha_inc.bedcook.models.Cake
import ru.ha_inc.bedcook.models.CakeDataSource
import ru.ha_inc.bedcook.models.Order
import ru.ha_inc.bedcook.utils.MutableLiveData2

class OrderViewModel : ViewModel() {

    private val _order = MutableLiveData<Order>()
    val order: LiveData<Order> get() = _order

    fun setOrder(order: Order) {
        _order.value = order
    }

}