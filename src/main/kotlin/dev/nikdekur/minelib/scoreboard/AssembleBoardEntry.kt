package dev.nikdekur.minelib.scoreboard

import org.bukkit.scoreboard.Team

/**
 * Assemble Board Entry
 *
 * @param board    that entry belongs to.
 * @param text     of entry.
 * @param position of entry.
 */
class AssembleBoardEntry(val board: AssembleBoard, var text: String, position: Int) {
    var team: Team? = null

    val identifier: String = board.getUniqueIdentifier(position)


    init {
        this.setup()
    }

    /**
     * Setup Board Entry.
     */
    fun setup() {
        val scoreboard = board.scoreboard

        var teamName = this.identifier

        // This shouldn't happen, but just in case.
        if (teamName.length > 16) {
            teamName = teamName.substring(0, 16)
        }

        var team: Team? = scoreboard.getTeam(teamName)

        // Register the team if it does not exist.
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName)
        }

        // Add the entry to the team.
        if (!team!!.entries.contains(this.identifier)) {
            team.addEntry(this.identifier)
        }

        // Add the entry if it does not exist.
        if (!board.entries.contains(this)) {
            board.entries.add(this)
        }

        this.team = team
    }

    /**
     * Send Board Entry Update.
     *
     * @param position of entry.
     */
    fun send(position: Int) {
        // Set Prefix & Suffix.
        val split = AssembleUtils.splitTeamText(text)
        team!!.prefix = split[0]
        team!!.suffix = split[1]

        // Set the score
        board.objective.getScore(this.identifier).score = position
    }

    /**
     * Remove Board Entry from Board.
     */
    fun remove() {
        board.identifiers.remove(this.identifier)
        board.scoreboard.resetScores(this.identifier)
    }
}
