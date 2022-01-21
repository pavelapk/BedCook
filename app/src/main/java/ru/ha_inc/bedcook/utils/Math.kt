package ru.ha_inc.bedcook.utils

import com.google.ar.sceneform.math.Vector3

object Math {
    fun Collection<Vector3>.average(): Vector3 {
        var x = 0f
        var y = 0f
        var z = 0f
        forEach {
            x += it.x
            y += it.y
            z += it.z
        }
        return Vector3(x / size, y / size, z / size)
    }

}