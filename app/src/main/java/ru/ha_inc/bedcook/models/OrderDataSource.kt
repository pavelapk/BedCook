package ru.ha_inc.bedcook.models

import com.google.android.gms.maps.model.LatLng

object OrderDataSource {
    val orders = listOf(
        Order(CakeDataSource.cakes[0], Complexity.EASY, 100, 5000, 56.4687043, 84.9452485)
    )
}