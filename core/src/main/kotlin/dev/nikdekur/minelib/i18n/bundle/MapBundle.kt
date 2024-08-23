package dev.nikdekur.minelib.i18n.bundle

import dev.nikdekur.minelib.i18n.Message
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.msg.I18nMessage
import dev.nikdekur.ndkore.map.MultiMap
import dev.nikdekur.ndkore.map.get
import dev.nikdekur.ndkore.placeholder.PlaceholderParser

class MapBundle(
    override val id: String,
    override val messages: Collection<I18nMessage>,
    val translations: MultiMap<Locale, String, String>
) : Bundle {

    override fun getMessage(locale: Locale, msg: I18nMessage, vararg placeholders: Pair<String, Any?>, parser: PlaceholderParser): Message? {
        val translation = translations[locale, msg.id] ?: return null
        return Message(translation).parsePlaceholders(parser, *placeholders)
    }
}