package ru.ha_inc.bedcook.map

import androidx.lifecycle.ViewModel
import ru.ha_inc.bedcook.models.OrderDataSource

class MapViewModel : ViewModel() {

    fun getOrders() = OrderDataSource.orders
}