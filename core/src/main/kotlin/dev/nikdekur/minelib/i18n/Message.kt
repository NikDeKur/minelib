@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.ext.applyColors
import dev.nikdekur.minelib.ext.bLogger
import dev.nikdekur.minelib.ext.sendActionBar
import dev.nikdekur.minelib.ext.sendSimpleMessage
import dev.nikdekur.ndkore.ext.isBlankOrEmpty
import dev.nikdekur.ndkore.ext.toTArray
import org.bukkit.command.CommandSender
import java.util.logging.Level

data class Message(
    val rawText: String
) {

    val text: String
        get() = rawText.applyColors()


    val listText: List<String>
        get() = text.split("\n")

    val arrayText: Array<out String>
        get() = listText.toTArray()


    fun send(sender: CommandSender) {
        sender.sendSimpleMessage(text, applyColors = false)
    }

    fun sendActionBar(player: CommandSender) {
        if (player is org.bukkit.entity.Player)
            player.sendActionBar(text, applyColors = false)
        else
            send(player)
    }

    fun sendTitle(player: CommandSender, fadeIn: Int = 10, stay: Int = 50, fadeOut: Int = 10) {
        val text = listText
        var title: String?
        var subtitle: String? = null
        if (text.size > 1) {
            title = text[0]
            subtitle = text[1]
        } else {
            title = text[0]
        }

        if (title.isBlankOrEmpty())
            title = null
        if (subtitle?.isBlankOrEmpty() == true)
            subtitle = null

        if (title == null && subtitle == null) return

        if (player is org.bukkit.entity.Player)
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut)
        else
            send(player)
    }

    @JvmOverloads
    inline fun log(level: Level = Level.INFO) {
        bLogger.log(level, text)
    }

    override fun toString(): String {
        return "Message(text='$text')"
    }
}
