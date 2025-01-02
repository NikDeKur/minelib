package dev.nikdekur.minelib.command.api

import dev.nikdekur.minelib.service.PluginComponent
import dev.nikdekur.ornament.i18n.Key
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import kotlin.time.Duration

interface ServerCommand : TabExecutor, PluginComponent {

    val name: String
    val aliases: Array<String>
        get() = emptyArray()

    val cooldown: Duration
        get() = Duration.ZERO

    fun hasCooldown(): Boolean = cooldown > Duration.ZERO

    val permission: String?
    val isConsoleFriendly: Boolean
    val argsRequirement: Int?
    val usageMSG: Key?


    fun CommandContext.onCommand()
    fun CommandTabContext.onTabComplete(): MutableList<String>? = null
    fun getUsage(sender: CommandSender): String = ""

    /**
     * Gets player's cooldown in milliseconds left.
     *
     * If a player has no cooldown, returns null.
     *
     * @param player Player to get cooldown for
     * @param command Command to get cooldown for
     * @return Cooldown left or null if no cooldown
     */
    fun getCooldown(player: Player): Duration?

    /**
     * Set player's cooldown for the command.
     *
     * @param player Player to set cooldown for
     * @param cooldown Duration of cooldown
     */
    fun setCooldown(player: Player, cooldown: Duration)


    /**
     * Reset player's cooldown for the command.
     *
     * @param player Player to reset cooldown for
     */
    fun resetCooldown(player: Player)

    class StopCommand : RuntimeException()
}