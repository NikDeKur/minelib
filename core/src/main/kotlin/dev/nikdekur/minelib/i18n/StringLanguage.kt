@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.ext.bLogger
import dev.nikdekur.minelib.i18n.Language.Code
import dev.nikdekur.ndkore.placeholder.Placeholder

open class StringLanguage(
    override val code: Code,
    val messages: MutableMap<String, String>
) : Language, Placeholder {

    override fun getPlaceholder(key: String): Any? {
        val message = getIfPresent(key)
        return message ?: super.getPlaceholder(key)
    }


    fun getIfPresent(id: String): String? {
        return messages[id]
    }

    override fun getMessage(msg: MSGHolder): Message? {
        return getIfPresent(msg.id)?.let { Message(it) }
    }

    override operator fun get(msg: MSGHolder): Message {
        return getMessage(msg) ?: run {
            bLogger.warning("Message '${msg.id}' not found in language '$code'! Trying to use default message!")
            Message(msg.defaultText)
        }
    }
}





