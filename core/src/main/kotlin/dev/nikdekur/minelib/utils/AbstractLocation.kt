@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)

package dev.nikdekur.minelib.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

@Serializable
data class AbstractLocation(
    var x: Double,
    var y: Double,
    var z: Double,
    var yaw: Float = 0f,
    var pitch: Float = 0f
) : Cloneable {

    fun add(x: Double, y: Double, z: Double): AbstractLocation {
        this.x += x
        this.y += y
        this.z += z
        return this
    }

    fun add(vector: Vector): AbstractLocation {
        return add(vector.x, vector.y, vector.z)
    }

    fun subtract(x: Double, y: Double, z: Double): AbstractLocation {
        this.x -= x
        this.y -= y
        this.z -= z
        return this
    }

    fun subtract(vector: Vector): AbstractLocation {
        return subtract(vector.x, vector.y, vector.z)
    }

    inline fun toVector(): Vector {
        return Vector(x, y, z)
    }

    inline fun toLocation(world: World): Location {
        return Location(world, x, y, z, yaw, pitch)
    }

    public override fun clone(): AbstractLocation {
        return AbstractLocation(x, y, z, yaw, pitch)
    }
}
