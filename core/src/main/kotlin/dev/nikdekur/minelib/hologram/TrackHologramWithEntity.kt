package dev.nikdekur.minelib.hologram

import dev.nikdekur.minelib.pentity.PersonalEntity
import dev.nikdekur.minelib.utils.AbstractLocation
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.UUID

data class EntityWithHologramImpl(
    override val entity: PersonalEntity,
    override val hologram: Hologram,
    val hologramOffset: (Player) -> Vector
) : EntityWithHologram {

    override val world: World
        get() = entity.world
    override val viewers: Set<Player>
        get() = entity.viewers


    override fun spawn(player: Player): Iterable<Entity> {
        val entities = entity.spawn(player)
        hologram.spawn(player)
        hologram.teleport(player, hologram.getLocation(player).copy().add(hologramOffset(player)))
        return entities
    }

    override fun spawnForEveryone() {
        entity.spawnForEveryone()
        hologram.spawnForEveryone()
    }

    override fun remove(player: Player) {
        entity.remove(player)
        hologram.remove(player)
    }

    override fun remove() {
        entity.remove()
        hologram.remove()
    }

    override fun teleport(player: Player, location: AbstractLocation) {
        entity.teleport(player, location)
        val offset = hologramOffset(player)
        hologram.teleport(player, location.copy().add(offset))
    }

    override fun getEntities(player: Player): Collection<Entity> {
        return entity.getEntities(player)
    }

    override fun getEntity(player: Player, entityId: Int): Entity? {
        return entity.getEntity(player, entityId) ?: hologram.getEntity(player, entityId)
    }

    override fun isVisibleFor(player: Player): Boolean {
        return entity.isVisibleFor(player) && hologram.isVisibleFor(player)
    }

    override fun shouldSpawn(player: Player): Boolean {
        return entity.shouldSpawn(player) && hologram.shouldSpawn(player)
    }

    override val id: UUID = entity.id


}


/**
 *
 *
 *     fun updateHologram(player: Player) {
 *         hologram.update(player)
 *     }
 *
 *
 *
 *     override fun spawn0(player: Player): Collection<Entity> {
 *         val entities = super.spawn0(player)
 *         val holograms = hologram.spawn0(player)
 *         teleportHologram(player, entities)
 *         debug("Spawn holograms (${holograms.size}) and entities (${entities.size}) for ${player.name}")
 *         return entities
 *     }
 *
 *     override fun remove0(player: Player) {
 *         super.remove0(player)
 *         hologram.remove0(player)
 *     }
 *
 *     override fun remove() {
 *         super.remove()
 *         hologram.remove()
 *     }
 *
 *     override fun updateTracking(player: Player) {
 *         super.updateTracking(player)
 *         hologram.updateTracking(player)
 *     }
 *
 *     override fun teleport(player: Player, location: Location) {
 *         super.teleport(player, location)
 *         teleportHologram(player, null)
 *     }
 *
 *     override fun isVisibleFor(player: Player): Boolean {
 *         return super.isVisibleFor(player) && hologram.isVisibleFor(player)
 *     }
 *
 *     override fun shouldSpawn(player: Player): Boolean {
 *         return super.shouldSpawn(player) && hologram.shouldSpawn(player)
 *     }
 *
 *
 *     abstract fun getHologramText(player: Player): Collection<String>
 *     abstract fun getHologramLocation(player: Player, stack: Iterable<Entity>): Location
 *
 *
 *     override fun toString(): String {
 *         return "HologramEntity(id=$id)"
 *     }
 */