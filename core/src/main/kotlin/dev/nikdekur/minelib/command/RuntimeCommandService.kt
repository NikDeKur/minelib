package dev.nikdekur.minelib.command

import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.command.api.CommandService
import dev.nikdekur.minelib.command.api.ServerCommand
import dev.nikdekur.minelib.service.PluginService
import org.bukkit.entity.Player
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class RuntimeCommandService(
    override val app: PluginApplication
) : PluginService(), CommandService {

    val cooldownMap: MutableMap<Player, MutableMap<ServerCommand, Long>> = mutableMapOf()

    override fun getCooldown(
        player: Player,
        command: ServerCommand
    ): Duration? {
        val ends = cooldownMap[player]?.get(command) ?: return null
        val now = System.currentTimeMillis()
        return (ends - now).milliseconds
    }

    override fun setCooldown(
        player: Player,
        command: ServerCommand,
        cooldown: Duration
    ) {
        val playerCooldowns = cooldownMap.getOrPut(player) { mutableMapOf() }
        val now = System.currentTimeMillis()
        val ends = now + cooldown.inWholeMilliseconds
        playerCooldowns[command] = ends
    }

    override fun resetCooldown(layer: Player, command: ServerCommand) {
        cooldownMap[layer]?.remove(command)
    }
}