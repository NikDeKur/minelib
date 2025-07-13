package dev.nikdekur.minelib.scoreboard

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import dev.nikdekur.minelib.scoreboard.events.AssembleBoardCreateEvent
import dev.nikdekur.minelib.scoreboard.events.AssembleBoardDestroyEvent
/**
 * Assemble Listener.
 *
 * @param assembleScoreboardService instance.
 */
class AssembleListener(private val assembleScoreboardService: AssembleScoreboardService) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // Call Events if enabled.
        if (assembleScoreboardService.isCallEvents) {
            val createEvent = AssembleBoardCreateEvent(event.player)

            Bukkit.getPluginManager().callEvent(createEvent)
            if (createEvent.isCancelled) {
                return
            }
        }

        assembleScoreboardService.boards[event.player.uniqueId] = AssembleBoard(event.player, assembleScoreboardService)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        // Call Events if enabled.
        if (assembleScoreboardService.isCallEvents) {
            val destroyEvent = AssembleBoardDestroyEvent(event.player)

            Bukkit.getPluginManager().callEvent(destroyEvent)
            if (destroyEvent.isCancelled) {
                return
            }
        }

        assembleScoreboardService.boards.remove(event.player.uniqueId)
        event.player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    }
}
