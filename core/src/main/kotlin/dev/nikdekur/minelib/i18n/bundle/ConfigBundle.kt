package dev.nikdekur.minelib.i18n.bundle

import dev.nikdekur.minelib.i18n.Message
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.ndkore.map.multi.MultiMap
import dev.nikdekur.ndkore.placeholder.PlaceholderParser

class ConfigBundle(
    override val id: String,
    override val messages: Collection<MSGHolder>,
    //                         Locale   MSG   Translation
    val translations: MultiMap<Locale, String, String>
) : Bundle {

    override fun getMessage(locale: Locale, key: MSGHolder, vararg placeholders: Pair<String, Any?>, parser: PlaceholderParser): Message? {
        val translation = translations[locale, key.id] ?: return null
        return Message(translation).parsePlaceholders(parser, *placeholders)
    }
}