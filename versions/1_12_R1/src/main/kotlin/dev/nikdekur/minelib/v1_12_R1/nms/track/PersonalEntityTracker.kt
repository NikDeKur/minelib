package dev.nikdekur.minelib.v1_12_R1.nms.track

import dev.nikdekur.minelib.v1_12_R1.pentity.TrackerPersonalEntity
import dev.nikdekur.ndkore.ext.r_GetField
import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.EntityTracker
import net.minecraft.server.v1_12_R1.EntityTrackerEntry
import org.bukkit.entity.Player
import java.util.*

class PersonalEntityTracker(val pentity: TrackerPersonalEntity) {

    val tracker: EntityTracker = pentity.worldNMS.tracker

    // Trackers Set
    @Suppress("UNCHECKED_CAST")
    val nmsEntries = tracker.r_GetField("c").value as MutableSet<EntityTrackerEntry>

    val viewMap = HashMap<Player, LinkedHashMap<Int, Entity>>()

    val viewers: Set<Player>
        get() = viewMap.keys


    fun track(player: Player, entity: Entity) {
        // Spigot view distance from config
        val viewDistance = tracker.r_GetField("e").value as Int

        viewMap.computeIfAbsent(player) { LinkedHashMap() }[entity.id] = entity

        val trackEntry = pentity.newTrackerEntry(player, entity, viewDistance)
        nmsEntries.add(trackEntry)
        tracker.trackedEntities.a(entity.id, trackEntry)
        trackEntry.scan()
    }



    fun untrack(player: Player): Collection<Entity> {
        val entities = viewMap[player] ?: return emptySet()

        val copy = LinkedHashMap(entities)
        copy.forEach { (id, entity) ->
            tracker.untrackEntity(entity)
            entities.remove(id)
        }

        if (entities.isEmpty())  {
            viewMap.remove(player)
        }

        return copy.values
    }


    /**
     * Untrack all entities from all players.
     *
     * Executes [untrack] for each player in [viewMap].
     *
     * @return List of untracked entities.
     */
    fun untrackAll(): List<Entity> {
        val entities = LinkedList(viewMap.values.map { it.values }).flatten()
        HashSet(viewMap.keys).forEach(this::untrack)
        return entities
    }


    fun isTracking(player: Player): Boolean {
        return viewMap.containsKey(player)
    }


    fun update(player: Player) {
        val entities = viewMap[player]?.let { LinkedHashMap(it) }
        if (entities.isNullOrEmpty()) return
        untrack(player)
        entities.forEach { track(player, it.value) }
    }


    fun updateAll() {
        HashSet(viewMap.keys).forEach(this::update)
    }
}