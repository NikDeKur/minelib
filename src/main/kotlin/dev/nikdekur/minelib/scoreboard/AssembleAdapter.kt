package dev.nikdekur.minelib.scoreboard

import org.bukkit.entity.Player

interface AssembleAdapter {
    /**
     * Get's the scoreboard title.
     *
     * @param player who's title is being displayed.
     * @return title.
     */
    fun getTitle(player: Player): String = getData(player).first()

    /**
     * Get's the scoreboard lines.
     *
     * @param player who's lines are being displayed.
     * @return lines.
     */
    fun getLines(player: Player): List<String> = getData(player).drop(1)

    /**
     * Get's the scoreboard data.
     * The First string is the Title
     * The rest are the lines
     *
     * @param player who's data is being displayed to.
     * @return data.
     */
    fun getData(player: Player): Array<String>
}
