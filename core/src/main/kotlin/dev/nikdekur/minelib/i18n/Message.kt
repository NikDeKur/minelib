@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.ext.applyColors
import dev.nikdekur.minelib.ext.bLogger
import dev.nikdekur.minelib.ext.sendSimpleMessage
import dev.nikdekur.ndkore.ext.isBlankOrEmpty
import dev.nikdekur.ndkore.ext.toTArray
import dev.nikdekur.ndkore.placeholder.PlaceholderParser
import dev.nikdekur.ndkore.placeholder.parse
import org.bukkit.command.CommandSender
import java.util.logging.Level

class Message(var text: String) {
    
    constructor(text: List<String>) : this(text.joinToString("\n"))

    /**
     * Raw text with prefix
     */
    val chatText: String
        get() = text

    fun parsePlaceholders(parser: PlaceholderParser, vararg placeholders: Pair<String, Any?>): Message {
        text = parser.parse(text, *placeholders)
        return this
    }

    val arrayText: Array<out String>
        get() = listText.toTArray()
    val listText: List<String>
        get() = text.split("\n")

    fun send(player: CommandSender) {
        player.sendSimpleMessage(chatText)
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
            player.sendTitle(title?.applyColors(), subtitle?.applyColors(), fadeIn, stay, fadeOut)
        else
            send(player)
    }

    @JvmOverloads
    inline fun log(level: Level = Level.INFO) {
        bLogger.log(level, chatText)
    }

    override fun toString(): String {
        return "Message(text='$chatText')"
    }
}
