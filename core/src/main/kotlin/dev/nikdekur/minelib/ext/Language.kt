@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.i18n.I18nService
import dev.nikdekur.minelib.i18n.Message
import dev.nikdekur.minelib.i18n.sender.SenderContext
import dev.nikdekur.minelib.i18n.locale.LocaleProvider
import dev.nikdekur.ornament.i18n.Key
import org.bukkit.command.CommandSender


inline fun I18nService.getLangMsg(key: Key) =
    Message(translateKey(key))

inline fun I18nService.getLangMsg(key: Key, sender: CommandSender) =
    getLangMsg(
        key = key.withLocale(getLocale(sender))
    )

inline fun I18nService.sendLangMsg(
    sender: CommandSender,
    key: Key
) {
    getLangMsg(key, sender).send(sender)
}


inline fun I18nService.sendTitleLangMsg(
    sender: CommandSender,
    key: Key,
) {
    getLangMsg(key, sender).sendTitle(sender)
}






inline fun LocaleProvider.getLangMsg(key: Key) =
    Message(translate(key))

inline fun SenderContext.sendLangMsg(
    key: Key
) {
    localeProvider.getLangMsg(key).send(sender)
}


inline fun SenderContext.sendTitleLangMsg(
    key: Key,
) {
    localeProvider.getLangMsg(key).sendTitle(sender)
}