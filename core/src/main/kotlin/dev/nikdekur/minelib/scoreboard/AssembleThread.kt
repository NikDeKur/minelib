package dev.nikdekur.minelib.scoreboard

import dev.nikdekur.minelib.ext.bLogger
import dev.nikdekur.minelib.ext.online
import dev.nikdekur.minelib.utils.debug
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

class AssembleThread internal constructor(val manager: ScoreboardManager) : Thread("Assemble Thread") {

    /**
     * Assemble Thread.
     *
     * @param assemble instance.
     */
    init {
        start()
    }

    val msDelay = manager.ticks * 50L



    override fun run() {
        while (true) {
            try {
                tick()
                sleep(msDelay)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Tick logic for thread.
     */
    private fun tick() {
        online.forEach(this::tickPlayer)
    }

    fun handleError(player: Player, msg: String, e: Exception) {
        bLogger.warning(msg)
        e.printStackTrace()
        manager.cooldownManager.step(player.uniqueId)
    }

    private fun tickPlayer(player: Player) {
        debug("Tick player ${player.name}")
        if (manager.cooldownManager.hasCooldown(player.uniqueId)) return

        val board = manager.boards[player.uniqueId] ?: return

        val scoreboard = try {
            board.scoreboard
        } catch (e: Exception) {
            handleError(player, "Failed to get scoreboard for player ${player.name}", e)
            return
        }
        val objective = board.objective

        val data = try {
            manager.adapter.getData(player)
        } catch (e: Exception) {
            handleError(player, "Failed to get scoreboard data for player ${player.name}", e)
            return
        }

        val (titleRaw, newLinesRaw) = try {
            data.first() to data.drop(1)
        } catch (e: Exception) {
            handleError(player, "Failed to parse scoreboard data for player ${player.name}", e)
            return
        }

        val title = ChatColor.translateAlternateColorCodes('&', titleRaw)



        try {
            // Update the title if needed.
            if (objective.displayName != title) {
                objective.displayName = title
            }

            var newLines = newLinesRaw
            if (newLines.isEmpty()) {
                board.entries.forEach(AssembleBoardEntry::remove)
                board.entries.clear()
            } else {
                if (newLines.size > 15) {
                    newLines = newLines.subList(0, 15)
                }

                // Reverse the lines because scoreboard scores are in descending order.
                if (!manager.style.isDescending) {
                    newLines = newLines.toMutableList()
                    newLines.reverse()
                }

                // Remove excessive number of board entries.
                if (board.entries.size > newLines.size) {
                    for (i in newLines.size until board.entries.size) {
                        val entry = board.getEntryAtPosition(i)

                        entry?.remove()
                    }
                }

                // Update existing entries / add new entries.
                var cache: Int = manager.style.startNumber
                for (i in newLines.indices) {
                    var entry = board.getEntryAtPosition(i)

                    // Translate any colours.
                    val line = ChatColor.translateAlternateColorCodes('&', newLines[i])

                    // If the entry is null, create a new one.
                    // Creating a new AssembleBoardEntry instance will add
                    // itself to the provided board's entry list.
                    if (entry == null) {
                        entry = AssembleBoardEntry(board, line, i)
                    }

                    // Update text, set up the team, and update the display values.
                    entry.text = line
                    entry.setup()
                    entry.send(
                        if (manager.style.isDescending)
                            cache--
                        else
                            cache++
                    )
                }
            }

            if (player.scoreboard !== scoreboard && !manager.isHook) {
                player.scoreboard = scoreboard
            }
        } catch (e: Exception) {
            // This exception occurs when a player scoreboard is not created before.
            // Usually it's caused by kicking a player before the scoreboard is created.
            // So we can safely ignore this exception.
            val msg = e.message
            if (msg != null && msg.contains("Asynchronous scoreboard creation", true))
                return

            handleError(player, "Failed to update scoreboard for player ${player.name}", e)
        }
    }
}
