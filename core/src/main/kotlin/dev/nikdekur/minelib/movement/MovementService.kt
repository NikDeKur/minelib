package dev.nikdekur.minelib.movement

import org.bukkit.Location
import org.bukkit.entity.Player

interface MovementService {
    /**
     * Teleport player to location and save new location as last location
     *
     * This prevents calling [OptiPlayerMoveEvent]
     * It Could be useful if you want to teleport a player to a specific location, while some listener cancelling any player moving.
     *
     * @param player Player to teleport
     * @param location Location to teleport player
     */
    fun teleport(player: Player, location: Location)

    /**
     * Teleport player to specific location thread-safely.
     *
     * Method will be executed in the main thread next tick.
     */
    fun teleportSafe(player: Player, location: Location)
}