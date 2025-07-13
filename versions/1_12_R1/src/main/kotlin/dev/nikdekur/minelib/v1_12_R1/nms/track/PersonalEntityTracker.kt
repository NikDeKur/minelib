package dev.nikdekur.minelib.v1_12_R1.nms.track

import dev.nikdekur.ndkore.ext.r_GetField
import dev.nikdekur.ndkore.map.MutableMultiMap
import dev.nikdekur.ndkore.map.put
import dev.nikdekur.ndkore.map.remove
import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.EntityTracker
import net.minecraft.server.v1_12_R1.EntityTrackerEntry
import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.entity.Player

open class PersonalEntityTracker(
    val world: WorldServer
) {

    val tracker: EntityTracker = world.tracker

    // Trackers Set
    val nmsTrackersSet: MutableSet<EntityTrackerEntry>
        @Suppress("UNCHECKED_CAST")
        get() = tracker.r_GetField("c").value as MutableSet<EntityTrackerEntry>

    // Shows what player actually must see
    val viewMap: MutableMultiMap<Player, Int, Entity> = HashMap()

    val viewers: Set<Player>
        get() = viewMap.keys

    /**
     * Creates a new tracker entry for the holder.
     *
     * EntityTracker contains values for render distance, update interval, and other settings.
     * It's recommended to use [PersonalTrackerEntry.Companion.factory] to create a new tracker entry for specific entity types.
     *
     * @param player The player to create the tracker entry.
     * @param entity The entity to create the tracker entry.
     * @param spigotViewDistance The view distance from server.properties.
     */
    open fun newTrackerEntry(player: Player, entity: Entity, spigotViewDistance: Int): PersonalTrackerEntry {
        return PersonalTrackerEntry.new(player, entity, spigotViewDistance)
    }

    fun track(player: Player, entity: Entity) {
        // Spigot view distance from config
        val viewDistance = tracker.r_GetField("e").value as Int

        check(!tracker.trackedEntities.b(entity.id)) { "Entity is already tracked!" }

        viewMap.put(player, entity.id, entity, ::linkedMapOf)

        val trackEntry = newTrackerEntry(player, entity, viewDistance)
        nmsTrackersSet.add(trackEntry)

        // Register for NMS the entity in register,
        // `a` is `put` for IntHashMap
        tracker.trackedEntities.a(entity.id, trackEntry)

        trackEntry.scan()
    }



    fun untrack(player: Player): Collection<Entity> {
        return getEntities(player)
            .onEach { entity ->
                tracker.untrackEntity(entity)
                viewMap.remove(player, entity.id)
            }
    }


    /**
     * Untrack all entities from all players.
     *
     * Executes [untrack] for each player in [viewMap].
     *
     * @return List of untracked entities.
     */
    fun untrackAll(): List<Entity> {
        val entities = viewMap.values.flatMap { it.values }
        viewers.toSet().forEach(this::untrack)
        return entities
    }


    fun isTrackingAnyEntities(player: Player): Boolean {
        return viewMap.containsKey(player)
    }


    fun reTrack(player: Player) {
        // untrack will remove the player from the viewMap, so we need to get the entities first
        val entities = getEntities(player)

        untrack(player)

        entities.forEach {
            track(player, it)
        }
    }


    fun updateAll() {
        viewers.toSet().forEach(this::reTrack)
    }

    fun getEntities(player: Player): Collection<Entity> {
        return viewMap[player]?.values?.toSet() ?: emptySet()
    }

    fun getEntity(player: Player, entityId: Int): Entity? {
        return viewMap[player]?.get(entityId)
    }
}