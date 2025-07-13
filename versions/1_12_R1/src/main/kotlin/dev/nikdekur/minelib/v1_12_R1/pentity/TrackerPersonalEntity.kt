package dev.nikdekur.minelib.v1_12_R1.pentity

import dev.nikdekur.minelib.pentity.PersonalEntityContext
import dev.nikdekur.minelib.pentity.PersonalEntity
import dev.nikdekur.minelib.utils.AbstractLocation
import dev.nikdekur.minelib.v1_12_R1.ext.nms
import dev.nikdekur.minelib.v1_12_R1.nms.track.PersonalEntityTracker
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

abstract class TrackerPersonalEntity<C : PersonalEntityContext>(
    override val id: UUID,
    override val world: World,
    val tracker: PersonalEntityTracker
) : PersonalEntity<C> {

    override val viewers: Set<Player>
        get() = tracker.viewers

    abstract fun createEntities(context: C): Collection<Entity>

    override fun spawn(context: C): Collection<Entity> {
        return createEntities(context)
            .onEach { entity ->
                tracker.track(context.player, entity.nms)
            }
    }

    override fun remove() {
        tracker.untrackAll()
    }

    override fun remove(player: Player) {
        tracker.untrack(player)
    }

    override fun teleport(player: Player, location: AbstractLocation) {
        getEntities(player).forEach {
            it.nms.setLocation(location.x, location.y, location.z, location.yaw, location.pitch)
        }
    }

    override fun getEntities(player: Player): Collection<Entity> {
        return tracker.getEntities(player).map { it.bukkitEntity }
    }

    override fun getEntity(player: Player, entityId: Int): Entity? {
        return tracker.getEntity(player, entityId)?.bukkitEntity
    }

    override fun isVisibleFor(player: Player): Boolean {
        return tracker.isTrackingAnyEntities(player)
    }
}