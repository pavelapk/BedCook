package ru.ha_inc.bedcook.models

data class OrderResult(
    val order: Order,
    val result1: Int,
    val result2: Int,
    val result3: Int
) : java.io.Serializable
