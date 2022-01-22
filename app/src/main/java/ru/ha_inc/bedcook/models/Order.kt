package ru.ha_inc.bedcook.models

import com.google.android.gms.maps.model.LatLng
import ru.ha_inc.bedcook.R

data class Order(
    val cake: Cake,
    val complexity: Complexity,
    val scoreBonus: Int,
    val salary: Int,
    val lat: Double,
    val lng: Double
) : java.io.Serializable


enum class Complexity(val level: Int) {
    EASY(0), NORMAL(1), HARD(2)
}