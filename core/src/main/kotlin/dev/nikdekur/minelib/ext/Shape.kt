package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.utils.AbstractLocation
import dev.nikdekur.ndkore.spatial.Point
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