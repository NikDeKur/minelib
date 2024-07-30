package dev.nikdekur.minelib.hologram

import net.minecraft.server.v1_12_R1.Entity
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import dev.nikdekur.minelib.pentity.PersonalEntityImpl
import dev.nikdekur.minelib.utils.Utils.debug
import dev.nikdekur.minelib.utils.Utils.zeroVector

abstract class HologramEntity(world: World) : PersonalEntityImpl(world) {

    val hologram = object : Hologram(world) {
        override fun getLocation(player: Player) = zeroVector
        override fun getText(player: Player) = this@HologramEntity.getHologramText(player)
    }

    fun teleportHologram(player: Player, entities: Iterable<Entity>?) {
        val stack = entities ?: getEntities(player)
        val location = getHologramLocation(player, stack)
        hologram.teleport(player, location)
    }

    fun updateHologram(player: Player) {
        hologram.update(player)
    }



    override fun spawn0(player: Player): Collection<Entity> {
        val entities = super.spawn0(player)
        val holograms = hologram.spawn0(player)
        teleportHologram(player, entities)
        debug("Spawn holograms (${holograms.size}) and entities (${entities.size}) for ${player.name}")
        return entities
    }

    override fun remove0(player: Player) {
        super.remove0(player)
        hologram.remove0(player)
    }

    override fun remove() {
        super.remove()
        hologram.remove()
    }

    override fun updateTracking(player: Player) {
        super.updateTracking(player)
        hologram.updateTracking(player)
    }

    override fun teleport(player: Player, location: Location) {
        super.teleport(player, location)
        teleportHologram(player, null)
    }

    override fun isVisibleFor(player: Player): Boolean {
        return super.isVisibleFor(player) && hologram.isVisibleFor(player)
    }

    override fun shouldSpawn(player: Player): Boolean {
        return super.shouldSpawn(player) && hologram.shouldSpawn(player)
    }


    abstract fun getHologramText(player: Player): Collection<String>
    abstract fun getHologramLocation(player: Player, stack: Iterable<Entity>): Location


    override fun toString(): String {
        return "HologramEntity(id=$id)"
    }
}