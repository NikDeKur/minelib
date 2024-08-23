package dev.nikdekur.minelib.i18n


import dev.nikdekur.minelib.i18n.bundle.Bundle
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.msg.I18nMessage
import dev.nikdekur.minelib.i18n.msg.MessageReference
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.ndkore.map.MultiMap
import dev.nikdekur.ndkore.placeholder.PlaceholderParser
import org.bukkit.command.CommandSender

interface I18nService : PluginService {

    fun addDataProvider(provider: PlayerLocaleProvider)
    fun getDataProvider(id: String): PlayerLocaleProvider?

    /**
     * Creates a new bundle with the given messages and translations.
     *
     * @param id The unique identifier of the bundle.
     * @param messages The messages in the bundle.
     * @param translations The default translations for the messages.
     * The first key is the locale, the second key is the message key, and the value is the translation.
     */
    fun loadBundle(
        id: String,
        messages: Collection<I18nMessage>,
        translations: MultiMap<Locale, I18nMessage, String>
    ): Bundle
    fun getBundle(id: String): Bundle?

    fun getMessage(
        locale: Locale,
        reference: MessageReference,
        vararg placeholders: Pair<String, Any?>,
        parser: PlaceholderParser
    ): Message

    fun getLocale(sender: CommandSender): Locale
}