package dev.nikdekur.minelib.drawing.shape

import org.bukkit.Location
import org.bukkit.Particle

abstract class AbstractShape(
    var center: Location,
    var particle: Particle,
    var particlesAmount : Int = 1,
    var xOffSet: Double = 0.0,
    var yOffSet: Double = 0.0,
    var zOffSet: Double = 0.0,
    var extra: Double = 1.0,
    var data: Any? = null
) : Shape {

    fun spawnAtLocation(x: Double, y: Double, z: Double) {
        center.world.spawnParticle(particle, x, y, z, particlesAmount, xOffSet, yOffSet, zOffSet, extra, data)
    }
}