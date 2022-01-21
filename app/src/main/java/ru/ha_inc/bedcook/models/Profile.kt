package ru.ha_inc.bedcook.models

data class Profile(
    var username: String? = null,
    var level: Int? = null,
    var score: Int? = null,
    var money: Int? = null
)
