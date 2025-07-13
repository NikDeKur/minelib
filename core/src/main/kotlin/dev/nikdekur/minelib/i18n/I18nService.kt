package dev.nikdekur.minelib.i18n


import dev.nikdekur.ndkore.map.MultiMap
import dev.nikdekur.ornament.i18n.Locale
import org.bukkit.command.CommandSender
import dev.nikdekur.ornament.i18n.I18nService as OrnamentI18nService

interface I18nService : OrnamentI18nService {

    // TODO: Move to a locale service
    fun addDataProvider(provider: PlayerLocaleProvider)
    fun getDataProvider(id: String): PlayerLocaleProvider?
    fun getLocale(sender: CommandSender): Locale


    /**
     * Creates a new bundle with the given messages and translations.
     *
     * If a bundle with the same id already exists, it will be overwritten.
     * The Method of overwriting a bundle will depend on [merge] parameter.
     * If [merge] is true, the overwriting will keep the previous translations and only add the new ones.
     * If [merge] is false, the overwriting will completely replace the previous translations with the new ones.
     *
     * @param id The unique identifier of the bundle.
     * @param translations The default translations for the messages.
     * The first key is the locale, the second key is the message key, and the value is the translation.
     * @param merge Whether to merge the new translations with the existing ones.
     */
    suspend fun saveBundle(
        id: String,
        translations: MultiMap<Locale, String, String>,
        merge: Boolean = false
    )

}