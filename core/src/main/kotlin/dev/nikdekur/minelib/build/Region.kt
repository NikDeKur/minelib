package dev.nikdekur.minelib.build

import dev.nikdekur.minelib.utils.AbstractLocation
import kotlinx.serialization.Serializable
import org.bukkit.Location

data class Region(
    val min: Location,
    val max: Location
)

@Serializable
data class AbstractRegion(
    val min: AbstractLocation,
    val max: AbstractLocation
)