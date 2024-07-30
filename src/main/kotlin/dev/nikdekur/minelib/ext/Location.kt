@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.utils.Utils
import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LightningStrike
import org.bukkit.entity.Player
import org.bukkit.util.Vector

inline fun Location.spawnEntity(entity: EntityType): Entity? {
    return world.spawnEntity(this, entity)
}

inline val Location.placeholder: Pair<String, Location>
    get() = "location" to this

inline fun Location.getNearbyBukkitEntities(x: Double, y: Double, z: Double): Collection<Entity> {
    return world.getNearbyEntities(this, x, y, z)
}

inline fun Location.getNearbyBukkitEntities(vector: Vector): Collection<Entity> {
    return world.getNearbyEntities(this, vector.x, vector.y, vector.z)
}

inline fun Location.getNearbyBukkitEntities(radius: Double): Collection<Entity> {
    return world.getNearbyEntities(this, radius, radius, radius)
}

@Suppress("UNCHECKED_CAST")
inline fun <T> Location.getNearbyBukkitEntities(clazz: Class<T>, x: Double, y: Double, z: Double): Collection<T> {
    return getNearbyBukkitEntities(x, y, z)
        .filter {
            it.javaClass.isAssignableFrom(clazz)
        } as Collection<T>
}

inline fun <T> Location.getNearbyBukkitEntities(clazz: Class<T>, vector: Vector) =
    getNearbyBukkitEntities(clazz, vector.x, vector.y, vector.z)
inline fun <T> Location.getNearbyBukkitEntities(clazz: Class<T>, radius: Double) =
    getNearbyBukkitEntities(clazz, radius, radius, radius)


inline fun Location.getNearbyPlayers(x: Double, y: Double, z: Double) =
    getNearbyBukkitEntities(Player::class.java, x, y, z)
inline fun Location.getNearbyPlayers(vector: Vector) =
    getNearbyBukkitEntities(Player::class.java, vector.x, vector.y, vector.z)
inline fun Location.getNearbyPlayers(radius: Double) =
    getNearbyBukkitEntities(Player::class.java, radius, radius, radius)

fun Location.removeEntities(radius: Double = 0.5) {
    getNearbyBukkitEntities(radius).forEach {
        if (it is Player) return
        it.remove()
    }
}

inline fun Location.strikeLightning(): LightningStrike {
    return world.strikeLightning(this)
}

inline fun Location.strikeLightningEffect(): LightningStrike {
    return world.strikeLightningEffect(this)
}

inline fun Location.createExplosion(power: Float, setFire: Boolean = false, breakBlocks: Boolean = false): Boolean {
    return world.createExplosion(this.x, this.y, this.z, power, setFire, breakBlocks)
}



inline val Vector.isZero: Boolean
    get() = this.x == 0.0 && this.y == 0.0 && this.z == 0.0




inline fun Location.asBlockLocation(): Location {
    x = blockX.toDouble()
    y = blockY.toDouble()
    z = blockZ.toDouble()
    return this
}

inline fun Number.asBlockLocation(): Int {
    return Location.locToBlock(this.toDouble())
}

inline val World.craft: CraftWorld
    get() = (this as CraftWorld)
inline val World.nms: WorldServer
    get() = craft.handle
inline val Location.nmsWorld: WorldServer
    get() = world.nms