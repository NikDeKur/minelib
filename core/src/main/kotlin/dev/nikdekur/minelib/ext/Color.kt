@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import org.bukkit.ChatColor
import org.bukkit.DyeColor

/**
 * Converts a [DyeColor] to a [ChatColor].
 *
 * This function is used to convert a [DyeColor] to a [ChatColor] for use in chat messages.
 * There is no guarantee that each [DyeColor] will have a corresponding [ChatColor], collisions are possible.
 *
 * The conversion is based on the color of the dye, and the closest matching [ChatColor] is used.
 * For example, the [DyeColor.WHITE] will be converted to [ChatColor.WHITE].
 *
 * @return The corresponding [ChatColor].
 * @see ChatColor
 */
inline fun DyeColor.toChatColor(): ChatColor {

    return when (this) {
        DyeColor.WHITE -> ChatColor.WHITE
        DyeColor.ORANGE -> ChatColor.GOLD
        DyeColor.BLACK -> ChatColor.BLACK
        DyeColor.YELLOW -> ChatColor.YELLOW
        DyeColor.LIME -> ChatColor.GREEN
        DyeColor.GREEN -> ChatColor.DARK_GREEN
        DyeColor.GRAY -> ChatColor.DARK_GRAY
        DyeColor.SILVER -> ChatColor.GRAY
        DyeColor.RED -> ChatColor.DARK_RED
        DyeColor.MAGENTA -> ChatColor.LIGHT_PURPLE
        DyeColor.PURPLE -> ChatColor.DARK_PURPLE
        DyeColor.LIGHT_BLUE -> ChatColor.AQUA
        DyeColor.CYAN -> ChatColor.DARK_AQUA
        DyeColor.BLUE -> ChatColor.BLUE


        // ChatColors with no corresponding DyeColor: RED, DARK_BLUE
        // No matches are left for: PINK, BROWN
        // So we use the closest matching color: PINK -> LIGHT_PURPLE, BROWN -> GOLD
        DyeColor.PINK -> ChatColor.LIGHT_PURPLE
        DyeColor.BROWN -> ChatColor.GOLD
    }
}