package dev.nikdekur.minelib.i18n


import dev.nikdekur.ndkore.map.MultiMap
import dev.nikdekur.ornament.i18n.Key
import dev.nikdekur.ornament.i18n.Locale
import org.bukkit.command.CommandSender
import dev.nikdekur.ornament.i18n.I18nService as OrnamentI18nService

interface I18nService : OrnamentI18nService {

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
    suspend fun saveBundle(
        id: String,
        messages: Collection<Key>,
        translations: MultiMap<Locale, String, String>
    )

    fun getLocale(sender: CommandSender): Locale
}