package dev.nikdekur.minelib.pentity

import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.Vec3D
import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import dev.nikdekur.minelib.nms.track.PersonalTrackerEntry
import dev.nikdekur.ndkore.`interface`.Snowflake
import java.util.*

interface PersonalEntity : Snowflake<UUID> {

    val bWorld: World
    val world: WorldServer

    val manager: PersonalEntityManager

    /**
     * The set of players that currently can see the entity.
     */
    val viewers: Set<Player>

    /**
     * Spawn the entity for the player.
     *
     * The entity will be spawned only for the specific player.
     *
     * @param player The player for which the entity will be spawned.
     * @return The spawned entities.
     */
    fun spawn(player: Player): Iterable<Entity>

    /**
     * Spawn the entity for all players in the world.
     *
     * Simply calls [spawn] for each player in the world.
     */
    fun spawnForEveryone()

    /**
     * Remove the entity for the player.
     *
     * The entity will be removed only for the specific player.
     *
     * @param player The player for which the entity will be removed.
     */
    fun remove(player: Player)


    /**
     * Remove the entity for all players in the world.
     *
     * Simply calls [remove] for each player in the world.
     *
     * Also, it will unregister the entity from the [PersonalEntityManager].
     * You don't have to worry about it. You can spawn it again using [spawn] method.
     */
    fun remove()

    /**
     * Teleport the entity to the specified position.
     *
     * If the entity is not spawned for the player, nothing will happen.
     *
     * @param player The player the entity is bounded to.
     * @param vector The position where the entity will be teleported.
     */
    fun teleport(player: Player, location: Location)

    /**
     * Get the entities bounded to the player.
     *
     * @param player The player to get the entities.
     * @return The collection of entities bounded to the player.
     */
    fun getEntities(player: Player): Collection<Entity>

    /**
     * Gets the entity by the entity minecraft id.
     *
     * @param player The player the entity is bounded to.
     * @param entityId The entity minecraft id.
     * @return The entity with the specified id or null if the entity is not found.
     */
    fun getEntity(player: Player, entityId: Int): Entity?


    /**
     * Check if the entity is visible for the player.
     *
     * @param player The player to check.
     * @return True if the entity is visible for the player, false otherwise.
     */
    fun isVisibleFor(player: Player): Boolean


    /**
     * Method, which will be called when the player joins the world or tracking, is updated.
     *
     * If the entity is already spawned for the player, this method will not be called.
     *
     * @param player The player to check.
     * @return True if the entity should be spawned for the player, false otherwise.
     */
    fun shouldSpawn(player: Player): Boolean

    /**
     * Update the entity tracking for the player.
     *
     * Method is used to force tracker to scan this player and add or remove the entity if needed.
     *
     * Method is called automatically when the player teleports (do this because of nms bug).
     *
     * @param player The player for which the entity will be updated.
     */
    fun updateTracking(player: Player)

    /**
     * Creates a new tracker entry for the entity.
     *
     * EntityTracker contains values for render distance, update interval, and other settings.
     * It's recommended to use [PersonalTrackerEntry.factory] to create a new tracker entry for specific entity types.
     *
     * @param player The player to create the tracker entry.
     * @param entity The entity to create the tracker entry.
     * @param spigotViewDistance The view distance from server.properties.
     */
    fun newTrackerEntry(player: Player, entity: Entity, spigotViewDistance: Int): PersonalTrackerEntry

    /**
     * Method, which will be called when the player right-clicks the entity.
     *
     * Be careful, this method is called when a packet from the player is received
     * and doesn't provide any additional checks.
     * It's highly recommended to add checks before custom logic to prevent spamming/crashing/cheating.
     *
     * @param player The player who right-clicked the entity.
     */
    fun onRightClick(player: Player) { /* Do Nothing by default */ }

    /**
     * Method, which will be called when the player right-clicks the entity at the specific position.
     *
     * Be careful, this method is called when a packet from the player is received
     * and doesn't provide any additional checks.
     * It's highly recommended to add checks before custom logic to prevent spamming/crashing/cheating.
     *
     * @param player The player who right-clicked the entity.
     * @param at The position where the player rights-clicked the entity.
     */
    fun onRightClickAt(player: Player, at: Vec3D) { /* Do Nothing by default */ }

    /**
     * Method, which will be called when the player left-clicks the entity.
     *
     * Be careful, this method is called when a packet from the player is received
     * and doesn't provide any additional checks.
     * It's highly recommended to add checks before custom logic to prevent spamming/crashing/cheating.
     *
     * @param player The player who left-clicked the entity.
     */
    fun onLeftClick(player: Player) { /* Do Nothing by default */ }
}