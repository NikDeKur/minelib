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
 * @param scoreboardManager instance.
 */
class AssembleListener(private val scoreboardManager: ScoreboardManager) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        // Call Events if enabled.
        if (scoreboardManager.isCallEvents) {
            val createEvent = AssembleBoardCreateEvent(event.player)

            Bukkit.getPluginManager().callEvent(createEvent)
            if (createEvent.isCancelled) {
                return
            }
        }

        scoreboardManager.boards[event.player.uniqueId] = AssembleBoard(event.player, scoreboardManager)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        // Call Events if enabled.
        if (scoreboardManager.isCallEvents) {
            val destroyEvent = AssembleBoardDestroyEvent(event.player)

            Bukkit.getPluginManager().callEvent(destroyEvent)
            if (destroyEvent.isCancelled) {
                return
            }
        }

        scoreboardManager.boards.remove(event.player.uniqueId)
        event.player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    }
}
