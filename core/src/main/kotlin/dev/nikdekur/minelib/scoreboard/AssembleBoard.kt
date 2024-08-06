package dev.nikdekur.minelib.scoreboard

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard
import dev.nikdekur.minelib.scoreboard.events.AssembleBoardCreatedEvent

/**
 * Assemble Board.
 *
 * @param player that the board belongs to.
 * @param scoreboardManager instance.
 */
class AssembleBoard(val player: Player, private val scoreboardManager: ScoreboardManager) {
    val entries = ArrayList<AssembleBoardEntry>()
    val identifiers = ArrayList<String>()


    init {
        this.setup(player)
    }

    val scoreboard: Scoreboard
        /**
         * Get's a player's bukkit scoreboard.
         *
         * @return either existing scoreboard or new scoreboard.
         */
        get() {
            return if (scoreboardManager.isHook || player.scoreboard !== Bukkit.getScoreboardManager().mainScoreboard) {
                player.scoreboard
            } else {
                Bukkit.getScoreboardManager().newScoreboard
            }
        }

    val objective: Objective
        /**
         * Get's the player's scoreboard objective.
         *
         * @return either existing objecting or new objective.
         */
        get() {
            val scoreboard = scoreboard
            if (scoreboard.getObjective("Assemble") == null) {
                val objective: Objective = scoreboard.registerNewObjective("Assemble", "dummy")
                objective.displaySlot = DisplaySlot.SIDEBAR
                objective.displayName = scoreboardManager.adapter.getTitle(player)
                return objective
            } else {
                return scoreboard.getObjective("Assemble")
            }
        }

    /**
     * Setup the board.
     *
     * @param player who's board to setup.
     */
    private fun setup(player: Player) {
        player.scoreboard = scoreboard

        // Call Events if enabled.
        if (scoreboardManager.isCallEvents) {
            val createdEvent = AssembleBoardCreatedEvent(this)
            Bukkit.getPluginManager().callEvent(createdEvent)
        }
    }

    /**
     * Get the board entry at a specific position.
     *
     * @param pos to find entry.
     * @return entry if it isn't out of range.
     */
    fun getEntryAtPosition(pos: Int): AssembleBoardEntry? {
        return if (pos >= entries.size) null else entries[pos]
    }

    /**
     * Get the unique identifier for position in scoreboard.
     *
     * @param position for identifier.
     * @return unique identifier.
     */
    fun getUniqueIdentifier(position: Int): String {
        var identifier = getRandomChatColor(position) + ChatColor.WHITE

        while (identifiers.contains(identifier)) {
            identifier = identifier + getRandomChatColor(position) + ChatColor.WHITE
        }

        // This is rare, but just in case, make the method recursive
        if (identifier.length > 16) {
            return this.getUniqueIdentifier(position)
        }

        // Add our identifier to the list so there are no duplicates
        identifiers.add(identifier)

        return identifier
    }

    /**
     * Gets a ChatColor based off the position in the collection.
     *
     * @param position of entry.
     * @return ChatColor adjacent to position.
     */
    private fun getRandomChatColor(position: Int): String {
        return ChatColor.entries[position].toString()
    }
}
