@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import dev.nikdekur.minelib.i18n.Language
import dev.nikdekur.minelib.i18n.LanguagesService
import dev.nikdekur.minelib.i18n.MSGHolder
import dev.nikdekur.minelib.i18n.Message
import dev.nikdekur.minelib.koin.getKoin
import org.bukkit.command.CommandSender


inline var CommandSender.language: Language
    get() = getKoin().get<LanguagesService>().getLanguage(this)
    set(value) = getKoin().get<LanguagesService>().setLanguage(this, value)

inline fun CommandSender.getLangMsg(msg: MSGHolder): Message {
    return language[msg]
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
