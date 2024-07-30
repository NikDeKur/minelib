@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import org.bukkit.command.CommandSender

inline fun CommandSender.sendSimpleMessage(message: String) {
    sendMessage(message.applyColors())
}