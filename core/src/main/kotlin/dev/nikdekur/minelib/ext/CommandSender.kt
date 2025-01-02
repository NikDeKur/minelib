@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import org.bukkit.command.CommandSender

inline fun CommandSender.sendSimpleMessage(message: Any, applyColors: Boolean = true) {
    sendMessage(message.toString().let { if (applyColors) it.applyColors() else it })
}