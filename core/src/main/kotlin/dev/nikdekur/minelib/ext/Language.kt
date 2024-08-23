@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.i18n.I18nService
import dev.nikdekur.minelib.i18n.Message
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.msg.MessageReference
import dev.nikdekur.ndkore.placeholder.PatternPlaceholderParser
import dev.nikdekur.ndkore.placeholder.PlaceholderParser
import dev.nikdekur.ndkore.service.getService
import org.bukkit.command.CommandSender


inline val CommandSender.locale: Locale
    get() = MineLib.instance.servicesManager.getService<I18nService>().getLocale(this)

inline fun CommandSender.getLangMsg(
    msg: MessageReference,
    vararg placeholders: Pair<String, Any?>,
    parser: PlaceholderParser = PatternPlaceholderParser.CURLY_BRACKET
): Message {
    val service = MineLib.instance.servicesManager.getService<I18nService>()
    return service.getMessage(locale, msg, *placeholders, parser = parser)
}


inline fun CommandSender.sendLangMsg(
    msg: MessageReference,
    vararg placeholders: Pair<String, Any?>,
    parser: PlaceholderParser = PatternPlaceholderParser.CURLY_BRACKET,
) {
    getLangMsg(msg, *placeholders, parser = parser).send(this)
}


inline fun CommandSender.sendTitleLangMsg(
    msg: MessageReference,
    vararg placeholders: Pair<String, Any?>,
    parser: PlaceholderParser = PatternPlaceholderParser.CURLY_BRACKET
) {
    getLangMsg(msg, *placeholders, parser = parser).sendTitle(this)
}