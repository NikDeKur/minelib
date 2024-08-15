@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.i18n.LanguagesService
import dev.nikdekur.minelib.i18n.Message
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.koin.getKoin
import org.bukkit.command.CommandSender


inline val CommandSender.locale: Locale
    get() = getKoin().get<LanguagesService>().getLanguage(this)

inline fun CommandSender.getLangMsg(msg: MSGHolder): Message {
    return getKoin().get<LanguagesService>().getBundle(msg.id)?.getMessage(locale, msg) ?: Message(msg.defaultText)
}

inline fun CommandSender.getLangMsg(msg: MSGHolder, vararg placeholders: Pair<String, Any?>): Message {
    return getLangMsg(msg).parsePlaceholders(*placeholders)
}


inline fun CommandSender.sendLangMsg(msg: MSGHolder) {
    getLangMsg(msg).send(this)
}

inline fun CommandSender.sendLangMsg(msg: MSGHolder, vararg placeholders: Pair<String, Any?>) {
    getLangMsg(msg, *placeholders).send(this)
}


inline fun CommandSender.sendTitleLangMsg(msg: MSGHolder) {
    getLangMsg(msg).sendTitle(this)
}
inline fun CommandSender.sendTitleLangMsg(msg: MSGHolder, vararg placeholders: Pair<String, Any>) {
    getLangMsg(msg, *placeholders).sendTitle(this)
}
