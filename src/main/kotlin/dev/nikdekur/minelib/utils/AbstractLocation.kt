@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)

package dev.nikdekur.minelib.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.World

@Serializable
data class AbstractLocation(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float = 0f,
    val pitch: Float = 0f
) {

    inline fun toLocation(world: World): Location {
        return Location(world, x, y, z, yaw, pitch)
    }
}
