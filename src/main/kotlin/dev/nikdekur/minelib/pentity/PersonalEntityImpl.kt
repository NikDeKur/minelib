@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.pentity

import dev.nikdekur.minelib.ext.nms
import dev.nikdekur.minelib.nms.track.PersonalEntityTracker
import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

/**
 * A personal entity is an entity that is only visible to a specific player
 * or entity looks different for each player.
 *
 * This class doesn't represent especially an entity, it can represent a group of entities.
 *
 * It is useful for creating custom entities that are only visible to a specific player:
 * - NPCs
 * - Holograms
 *
 * The personal entity object can be created only for one world.
 * You have to create a new object for each world.
 *
 * @param bWorld The bukkit world where the entity will be spawned.
 */
abstract class PersonalEntityImpl(
    final override val bWorld: World
) : PersonalEntity {

    final override val manager = ServerPersonalEntityManager.getManager(bWorld)

    /**
     * The unique identifier of the entity.
     *
     * It is unique for each entity, it's different from the minecraft entity id.
     */
    override val id: UUID = UUID.randomUUID()

    /**
     * The nms world of the bukkit world.
     */
    override val world: WorldServer by lazy {
        bWorld.nms
    }


    init {
        // Doesn't matter, it just saves the entity to hashmap
        @Suppress("LeakingThis")
        manager.registerEntity(this)
    }



    var removed = false
    inline fun checkRemoved() {
        check(!removed) { "The entity is removed! Create a new instance." }
    }



    open val tracker by lazy { PersonalEntityTracker(this) }

    /**
     * Abstract method to create a new entities for the player.
     *
     * Implement this method to create a logic for spawning the entities for different players.
     *
     * Entities could have any position, rotation, but it should be in the [world] world.
     *
     * @param player The player for which the entity will be spawned.
     * @return The entities for the player.
     */
    abstract fun newStack(player: Player): Collection<Entity>

    /**
     * Spawn the entity for the player.
     *
     * The entity will be spawned only for the specific player.
     *
     * @param player The player for which the entity will be spawned.
     * @return The spawned entities.
     */
    override fun spawn(player: Player): Collection<Entity> {
        checkRemoved()
        return spawn0(player)
    }

    /**
     * Protected method to spawn the entity for the player.
     *
     * Does not check if [PersonalEntityImpl] is registered.
     *
     * Don't try to call this method directly, use [spawn] instead.
     */
    open fun spawn0(player: Player): Collection<Entity> {
        val stack = newStack(player)
        stack.forEach {
            it.world = world
            manager.registerPersonalEntity(this, it)
            tracker.track(player, it)
        }
        return stack
    }

    /**
     * Spawn the entity for all players in the world.
     *
     * Simply calls [spawn] for each player in the world.
     */
    override fun spawnForEveryone() {
        checkRemoved()
        bWorld.players.forEach(::spawn0)
    }

    /**
     * Remove the entity for the player.
     *
     * The entity will be removed only for the specific player.
     *
     * @param player The player for which the entity will be removed.
     */
    override fun remove(player: Player) {
        checkRemoved()
        remove0(player)
    }

    /**
     * Protected method to remove the entity for the player.
     *
     * Does not check if [PersonalEntityImpl] is registered.
     *
     * Don't try to call this method directly, use [remove] instead.
     */
    open fun remove0(player: Player) {
        tracker.untrack(player)
    }

    /**
     * Remove the entity for all players in the world.
     *
     * Simply calls [remove] for each player in the world.
     *
     * Also, it will unregister the entity from the [PersonalEntityManager].
     * You don't have to worry about it. You can spawn it again using [spawn] method.
     */
    override fun remove() {
        checkRemoved()
        HashSet(tracker.viewers).forEach(::remove0)

        manager.unregisterEntity(id)
    }

    /**
     * Teleport the entity to the specified position.
     *
     * If the entity is not spawned for the player, nothing will happen.
     *
     * @param player The player the entity is bounded to.
     * @param vector The position where the entity will be teleported.
     */
    override fun teleport(player: Player, location: Location) {
        checkRemoved()

        getEntities(player).forEach {
            it.setLocation(location.x, location.y, location.z, location.yaw, location.pitch)
        }
    }

    /**
     * Get the entities bounded to the player.
     *
     * @param player The player to get the entities.
     * @return The collection of entities bounded to the player.
     */
    override fun getEntities(player: Player): Collection<Entity> {
        return tracker.viewMap[player]?.values ?: emptyList()
    }

    /**
     * Gets the entity by the entity minecraft id.
     *
     * @param player The player the entity is bounded to.
     * @param entityId The entity minecraft id.
     * @return The entity with the specified id or null if the entity is not found.
     */
    override fun getEntity(player: Player, entityId: Int): Entity? {
        return tracker.viewMap[player]?.get(entityId)
    }


    /**
     * Get the viewers of the entity.
     *
     * @return The set of players who can see the entity.
     */
    override val viewers: Set<Player>
        get() = tracker.viewers

    /**
     * Check if the entity is visible for the player.
     *
     * @param player The player to check.
     * @return True if the entity is visible for the player, false otherwise.
     */
    override fun isVisibleFor(player: Player): Boolean {
        return tracker.isTracking(player)
    }

    /**
     * Method, which will be called when the player joins the world or tracking, is updated.
     *
     * If the entity is already spawned for the player, this method will not be called.
     *
     * @param player The player to check.
     * @return True if the entity should be spawned for the player, false otherwise.
     */
    override fun shouldSpawn(player: Player): Boolean {
        return false
    }


    /**
     * Update the entity tracking for the player.
     *
     * Method is used to force tracker to scan this player and add or remove the entity if needed.
     *
     * Method is called automatically when the player teleports (do this because of nms bug).
     *
     * If the player is not tracking the entity, nothing will happen.
     *
     * @param player The player for which the entity will be updated.
     */
    override fun updateTracking(player: Player) {
        checkRemoved()
        if (!tracker.isTracking(player)) return
        tracker.update(player)
    }



    override fun toString(): String {
        return "PersonalEntityImpl(id=$id)"
    }

}