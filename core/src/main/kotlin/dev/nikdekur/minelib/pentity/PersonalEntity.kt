package dev.nikdekur.minelib.pentity

import dev.nikdekur.minelib.utils.AbstractLocation
import dev.nikdekur.ndkore.`interface`.Snowflake
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

interface PersonalEntity : Snowflake<UUID> {

    val world: World

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
     * Also, it will unregister the entity from the [dev.nikdekur.minelib.pentity.PersonalEntityManagerImpl].
     * You don't have to worry about it. You can spawn it again using [spawn] method.
     */
    fun remove()

    /**
     * Teleport the entity to the specified position.
     *
     * If the entity is not spawned for the player, nothing will happen.
     *
     * @param player The player the entity is bounded to.
     * @param location The position where the entity will be teleported.
     */
    fun teleport(player: Player, location: AbstractLocation)

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
     * Method, which will be called when the player right-clicks the entity.
     *
     * This method contains checks for the distance between the player and the entity to prevent cheating.
     * But no more.
     * Be careful with this method.
     *
     * @receiver The context of the right-click event.
     */
    fun ClickContext.Right.onRightClick() { /* Do Nothing by default */ }

    /**
     * Method, which will be called when the player right-clicks the entity at the specific position.
     *
     * This method contains checks for the distance between the player and the entity to prevent cheating.
     * But no more.
     * Be careful with this method.
     *
     * @receiver The context of the right-at-click event.
     */
    fun ClickContext.RightAt.onRightAtClick() { /* Do Nothing by default */ }

    /**
     * Method, which will be called when the player left-clicks the entity.
     *
     * This method contains checks for the distance between the player and the entity to prevent cheating.
     * But no more.
     * Be careful with this method.
     *
     * @receiver The context of the left-click event.
     */
    fun ClickContext.Left.onLeftClick() { /* Do Nothing by default */ }
}