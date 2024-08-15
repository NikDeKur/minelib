package dev.nikdekur.minelib.i18n


import dev.nikdekur.minelib.i18n.bundle.Bundle
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import org.bukkit.command.CommandSender

interface LanguagesService {

    fun addDataProvider(provider: PlayerLocaleProvider)
    fun getDataProvider(id: String): PlayerLocaleProvider?

    fun newBundle(id: String, messages: Collection<MSGHolder>)
    fun getBundle(id: String): Bundle?

    fun getLanguage(sender: CommandSender): Locale
}

//inline fun <E : Enum<out MSGHolder>> LanguagesService.importEnumMessages(enum: Class<E>) {
//    require(enum.isEnum) { "importFromEnum function can only import messages from enum" }
//    @Suppress("UNCHECKED_CAST")
//    val entries = enum.enumConstants as Array<out MSGHolder>
//    entries.forEach(::addMessage)
//}