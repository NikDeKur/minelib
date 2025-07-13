package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.utils.AbstractLocation
import dev.nikdekur.ndkore.spatial.ExclusiveCuboidShape
import dev.nikdekur.ndkore.spatial.Point
import dev.nikdekur.ndkore.spatial.add
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location
import org.bukkit.util.Vector

inline val AbstractLocation.point: Point
    get() = Point(x, y, z)

inline val Location.point: Point
    get() = Point(x, y, z)

inline val Vector.point: Point
    get() = Point(x, y, z)

inline val Point.location: AbstractLocation
    get() = AbstractLocation(x, y, z)



interface MinecraftCuboidShape : ExclusiveCuboidShape {
    val blockMax: Point

    override val max: Point
        get() = blockMax.add(1.0, 1.0, 1.0)
}

@SerialName("minecraft_cuboid")
@Serializable
data class MinecraftCuboidShapeData(
    override val min: Point,
    @SerialName("max")
    override val blockMax: Point
) : MinecraftCuboidShape