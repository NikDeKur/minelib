package dev.nikdekur.minelib.drawing.shape

import org.bukkit.Location
import org.bukkit.Particle
import kotlin.math.cos
import kotlin.math.sin

class CircleShape(
    particle: Particle,
    center: Location,
    val radius: Double,
    particlesAmount : Int = 1,
    val step: Int = 3,
    xOffSet: Double = 0.0,
    yOffSet: Double = 0.0,
    zOffSet: Double = 0.0,
    extra: Double = 1.0,
    data: Any? = null
) : AbstractShape(center, particle, particlesAmount, xOffSet, yOffSet, zOffSet, extra, data) {

    override fun draw() {
        for (i in 0 until 360 step step) {
            val x = center.x + radius * cos(Math.toRadians(i.toDouble()))
            val z = center.z + radius * sin(Math.toRadians(i.toDouble()))
            spawnAtLocation(x, center.y, z)
        }
    }
}