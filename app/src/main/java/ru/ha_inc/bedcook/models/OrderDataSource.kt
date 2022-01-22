package ru.ha_inc.bedcook.models

import com.google.android.gms.maps.model.LatLng

object OrderDataSource {
    val orders = listOf(
        Order(CakeDataSource.cakes[0], Complexity.EASY, 100, 5000, 56.4687043, 84.9452485),
        Order(CakeDataSource.cakes[0], Complexity.EASY, 100, 5000, 56.469368, 84.951280),
        Order(CakeDataSource.cakes[0], Complexity.EASY, 100, 5000, 56.468940, 84.960550),
        Order(CakeDataSource.cakes[0], Complexity.NORMAL, 150, 10000, 56.472183, 84.952138),
        Order(CakeDataSource.cakes[0], Complexity.NORMAL, 150, 10000, 56.478232, 84.960800),
        Order(CakeDataSource.cakes[0], Complexity.NORMAL, 150, 10000, 56.475581, 84.966529),
        Order(CakeDataSource.cakes[0], Complexity.HARD, 200, 15000, 56.470568, 84.962266),
        Order(CakeDataSource.cakes[0], Complexity.HARD, 200, 15000, 56.467327, 84.945825),
        Order(CakeDataSource.cakes[0], Complexity.HARD, 200, 15000, 56.473643, 84.945793)
    )
}