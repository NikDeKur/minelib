@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.i18n.config

import dev.nikdekur.minelib.ext.pairs
import dev.nikdekur.minelib.i18n.BukkitLocaleProvider
import dev.nikdekur.minelib.i18n.I18nService
import dev.nikdekur.minelib.i18n.Message
import dev.nikdekur.minelib.i18n.PlayerLocaleProvider
import dev.nikdekur.minelib.i18n.bundle.Bundle
import dev.nikdekur.minelib.i18n.bundle.MapBundle
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.locale.Locale.Companion.fromCode
import dev.nikdekur.minelib.i18n.locale.LocaleConfig
import dev.nikdekur.minelib.i18n.msg.I18nMessage
import dev.nikdekur.minelib.i18n.msg.MessageReference
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.plugin.loadConfig
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.ndkore.ext.addById
import dev.nikdekur.ndkore.map.MultiMap
import dev.nikdekur.ndkore.map.MutableMultiMap
import dev.nikdekur.ndkore.map.put
import dev.nikdekur.ndkore.placeholder.JVMReflectValuesSource
import dev.nikdekur.ndkore.placeholder.PatternPlaceholderParser
import dev.nikdekur.ndkore.placeholder.PlaceholderParser
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigI18nService(
    override val app: ServerPlugin
) : PluginService(), I18nService {

    override val bindClass
        get() = I18nService::class

    val bundles = HashMap<String, Bundle>()

    val defaultDataProvider = BukkitLocaleProvider

    var dataProviderId: String? = null
    private var _dataProvider: PlayerLocaleProvider? = null

    lateinit var defaultParser: PatternPlaceholderParser

    val dataProvider: PlayerLocaleProvider
        get() = _dataProvider ?: run {
            if (dataProviderId == null) {
                app.logger.severe("Locale data provider not found! Using $defaultDataProvider!")
                _dataProvider = defaultDataProvider
                return defaultDataProvider
            }

            val provider = dataProviders[dataProviderId]
            _dataProvider = provider
            provider ?: run {
                app.logger.warning("Locale data provider '$dataProviderId' not found! Using $defaultDataProvider!")
                defaultDataProvider
            }
        }

    lateinit var defaultLocale: Locale

    val dataProviders = HashMap<String, PlayerLocaleProvider>()

    val bundlesDir = File(app.dataFolder.parentFile.parentFile, "i18n/bundles")

    override fun onEnable() {

        defaultParser = PatternPlaceholderParser("{", "}", JVMReflectValuesSource)

        // Go back from plugin folder to the server folder
        val containerDir = app.dataFolder.parentFile.parentFile
        val i18nDir = File(containerDir, "i18n")
        val config = app.loadConfig<LocaleConfig>("config", folder = i18nDir)

        addDataProvider(BukkitLocaleProvider)

        dataProviderId = config.dataProvider

        // Create the bundles directory if it doesn't exist
        bundlesDir.mkdirs()

        val defaultLangCodeStr = config.defaultLocale
        this.defaultLocale = defaultLangCodeStr.let(Locale::fromCodeOrThrow)

        val bundlesDirs = bundlesDir.listFiles { file: File ->
            file.isDirectory
        } ?: emptyArray()

        bundlesDirs.forEach {
            val translations = loadBundleTranslations(it)
            val meta = app.loadConfig<MetaDataConfig>("metadata", folder = it)
            if (meta.defaults.isEmpty()) return@forEach
            val messages = meta.defaults.map {
                object : I18nMessage {
                    override val id = it.key
                    override val defaultText = it.value
                }
            }

            val bundle = MapBundle(it.name, messages, translations)

            loadBundleInternal(bundle)
        }
    }

    fun loadBundleTranslations(file: File): MultiMap<Locale, String, String> {
        val files = file.listFiles { file ->
            file.extension == "yml" && file.nameWithoutExtension != "metadata"
        } ?: emptyArray()

        val bundleMap: MutableMultiMap<Locale, String, String> = HashMap()

        files.forEach {
            val localeStr = it.nameWithoutExtension
            val locale = fromCode(localeStr) ?: return@forEach
            val langConfig = YamlConfiguration.loadConfiguration(it)

            langConfig.pairs.forEach {
                // Decode the message to a string
                val message = when (val o = it.value) {
                    is String -> o
                    is Collection<*> -> o.joinToString("\n")
                    else -> null
                } ?: return@forEach

                val msgKey = it.key

                bundleMap.put(locale, msgKey, message)
            }
        }

        return bundleMap
    }

    override fun onDisable() {
        bundles.clear()
    }




    override fun addDataProvider(provider: PlayerLocaleProvider) {
        dataProviders.addById(provider)
    }

    override fun getDataProvider(id: String): PlayerLocaleProvider? {
        return dataProviders[id]
    }




    fun loadBundleInternal(bundle: Bundle) {
        bundles[bundle.id] = bundle
    }

    override fun loadBundle(
        id: String,
        messages: Collection<I18nMessage>,
        translations: MultiMap<Locale, I18nMessage, String>
    ): Bundle {
        val bundleName = id.lowercase()
        val bundleDir = File(bundlesDir, bundleName)
        bundleDir.mkdirs()

        // Save the metadata to be able to create the bundle later
        val defaultsMap = messages.associate { it.id to it.defaultText }
        val meta = MetaDataConfig(defaultsMap)
        app.saveConfig("metadata", meta, folder = bundleDir)

        // Save the translations
        translations.forEach { locale, translations ->
            val langFile = File(bundleDir, "${locale.code}.yml")
            val langConfig = YamlConfiguration.loadConfiguration(langFile)
            val defaultTranslationsMap = translations.mapKeys { it.key.id }

            // Check if there are any missing translations
            val anyMissed = defaultTranslationsMap.keys.any { it !in langConfig }

            // If there are no missing translations, skip the saving
            if (!anyMissed) return@forEach

            // If some translations are missing, recreate config to add them (in right order)
            // Fill the new config with all translations with priority to the existing ones
            val newConfig = YamlConfiguration()
            defaultTranslationsMap.forEach { (key, defaultValue) ->
                val value = langConfig[key] ?: defaultValue
                newConfig[key] = when (value) {
                    is String ->
                        if (value.contains("\n")) value.split("\n")
                        else value
                    is Collection<*> -> value
                    else -> value.toString()
                }
            }

            // Save the new config
            newConfig.save(langFile)
        }

        val map = loadBundleTranslations(bundleDir)
        val bundle = MapBundle(id, messages, map)

        loadBundleInternal(bundle)
        return bundle
    }

    override fun getBundle(id: String): Bundle? {
        return bundles[id]
    }

    override fun getMessage(
        locale: Locale,
        reference: MessageReference,
        vararg placeholders: Pair<String, Any?>,
        parser: PlaceholderParser?
    ): Message {
        val bundleId = reference.bundleId
        val msg = reference.msg
        val bundle = getBundle(bundleId)
        val message = bundle?.getMessage(locale, msg) ?: Message(msg.defaultText)
        val parser = parser ?: defaultParser
        return message.parsePlaceholders(parser, *placeholders)
    }


    override fun getLocale(sender: CommandSender): Locale {
        return dataProvider.getLocale(sender) ?: defaultLocale
    }

}
