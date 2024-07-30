@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*


inline fun UUID.toOfflinePlayer() = Bukkit.getOfflinePlayer(this)
inline fun UUID.toPlayerOrNull(): Player? = toOfflinePlayer().player
inline fun UUID.toPlayer() = toPlayerOrNull() ?: throw IllegalStateException("Player is not online")

