package dev.nikdekur.minelib.v1_12_R1.ext

import dev.nikdekur.ndkore.spatial.CuboidShape
import dev.nikdekur.ndkore.spatial.Point
import dev.nikdekur.ndkore.spatial.Shape
import net.minecraft.server.v1_12_R1.AxisAlignedBB

inline val AxisAlignedBB.shape: Shape
    get() = object : CuboidShape {
        override val min = Point(a, b, c)
        override val max = Point(d, e, f)
    }