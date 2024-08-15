package dev.nikdekur.minelib.command.api

import org.bukkit.entity.Player
import kotlin.time.Duration

interface CommandService {

    /**
     * Gets player's cooldown in milliseconds left.
     *
     * If a player has no cooldown, returns null.
     *
     * @param player Player to get cooldown for
     * @param command Command to get cooldown for
     * @return Cooldown left or null if no cooldown
     */
    fun getCooldown(player: Player, command: ServerCommand): Duration?

    /**
     * Set player's cooldown for the command.
     *
     * @param player Player to set cooldown for
     * @param command Command to set cooldown for
     * @param cooldown Duration of cooldown
     */
    fun setCooldown(player: Player, command: ServerCommand, cooldown: Duration)


    /**
     * Reset player's cooldown for the command.
     *
     * @param player Player to reset cooldown for
     * @param command Command to reset cooldown for
     */
    fun resetCooldown(layer: Player, command: ServerCommand)

}