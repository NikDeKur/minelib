@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import org.bukkit.ChatColor

inline fun String.applyColors(symbol: Char = '&'): String {
    return ChatColor.translateAlternateColorCodes(symbol, this)
}

inline fun Collection<String>.applyColors(symbol: Char = '&'): Collection<String> {
    return map(String::applyColors)
}

