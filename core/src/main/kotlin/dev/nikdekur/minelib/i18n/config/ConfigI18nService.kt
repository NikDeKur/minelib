@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.i18n.config

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.i18n.BukkitLocaleProvider
import dev.nikdekur.minelib.i18n.I18nService
import dev.nikdekur.minelib.i18n.PlayerLocaleProvider
import dev.nikdekur.minelib.i18n.locale.LocaleConfig
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.ndkore.ext.addById
import dev.nikdekur.ndkore.map.MultiMap
import dev.nikdekur.ndkore.placeholder.JVMReflectMethod
import dev.nikdekur.ndkore.placeholder.PatternPlaceholderParser
import dev.nikdekur.ndkore.service.dependencies
import dev.nikdekur.ndkore.service.inject
import dev.nikdekur.ornament.dataset.DataSetService
import dev.nikdekur.ornament.dataset.get
import dev.nikdekur.ornament.i18n.Key
import dev.nikdekur.ornament.i18n.Locale
import dev.nikdekur.ornament.i18n.toLanguageTag
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import dev.nikdekur.ornament.i18n.I18nService as OrnamentI18nService

class ConfigI18nService(
    override val app: PluginApplication
) : PluginService(), I18nService {

    override val dependencies = dependencies {
        dependsOn(DataSetService::class, MineLib.I18nQualifier)
        dependsOn(OrnamentI18nService::class, MineLib.Qualifier)
    }

    val i18nDataset: DataSetService by inject(MineLib.I18nQualifier)
    val i18n: OrnamentI18nService by inject(MineLib.Qualifier)

    val defaultDataProvider = BukkitLocaleProvider

    var dataProviderId: String? = null
    private var _dataProvider: PlayerLocaleProvider? = null

    lateinit var defaultParser: PatternPlaceholderParser

    val dataProvider: PlayerLocaleProvider
        get() = _dataProvider ?: run {
            if (dataProviderId == null) {
                app.logger.error { "Locale data provider not found! Using $defaultDataProvider!" }
                _dataProvider = defaultDataProvider
                return defaultDataProvider
            }

            val provider = dataProviders[dataProviderId]
            _dataProvider = provider
            provider ?: run {
                app.logger.warn { "Locale data provider '$dataProviderId' not found! Using $defaultDataProvider!" }
                defaultDataProvider
            }
        }

    val dataProviders = HashMap<String, PlayerLocaleProvider>()

    init {
        addDataProvider(BukkitLocaleProvider)
    }

    val i18nDir = File(app.dataFolder, "i18n")

    override suspend fun onEnable() {

        defaultParser = PatternPlaceholderParser("\\{", "\\}", JVMReflectMethod)

        // Go back from plugin folder to the server folder
        val config = i18nDataset.get<LocaleConfig>("config") ?: LocaleConfig()
        dataProviderId = config.dataProvider
    }

    override fun addDataProvider(provider: PlayerLocaleProvider) {
        dataProviders.addById(provider)
    }

    override fun getDataProvider(id: String): PlayerLocaleProvider? {
        return dataProviders[id]
    }



    override suspend fun saveBundle(
        id: String,
        messages: Collection<Key>,
        translations: MultiMap<Locale, String, String>
    ) {
        val bundleName = id.lowercase()
        val bundleDir = File(i18nDir, bundleName)
        bundleDir.mkdirs()

        // Save the translations
        translations.forEach { locale, translations ->
            val code = locale.toLanguageTag()
            val langFile = File(bundleDir, "${code}.yml")
            val langConfig = YamlConfiguration.loadConfiguration(langFile)
            val defaultTranslationsMap = translations.mapKeys { it.key }

            // Check if there are any missing translations
            val anyMissed = defaultTranslationsMap.keys.any { it !in langConfig }

            // If there are no missing translations, skip this locale
            if (!anyMissed) return@forEach

            // If some translations are missing, recreate config to add them (in right order)
            // Fill the new config with all translations with priority to the existing ones
            val newConfig = YamlConfiguration()
            defaultTranslationsMap.forEach { (key, defaultValue) ->
                val value = langConfig[key] ?: defaultValue
                newConfig[key] = when (value) {
                    is String -> if (value.contains("\n")) value.split("\n") else value
                    is Collection<*> -> value
                    else -> value.toString()
                }
            }

            // Save the new config
            newConfig.save(langFile)
        }

        // (i18n as Service).reload()
    }

//    override fun getMessage(
//        locale: Locale,
//        reference: MessageReference,
//        vararg placeholders: Pair<String, Any?>,
//        parser: PlaceholderParser?
//    ): Message {
//        val bundleId = reference.bundleId
//        val msg = reference.msg
//        val bundle = getBundle(bundleId)
//        val message = bundle?.getMessage(locale, msg) ?: Message(msg.defaultText)
//        val parser = parser ?: defaultParser
//        return message.parsePlaceholders(parser, *placeholders)
//    }



    override fun getLocale(sender: CommandSender): Locale {
        return dataProvider.getLocale(sender) ?: defaultLocale
    }


    override val defaultLocale: Locale
        get() = i18n.defaultLocale

    override fun hasKey(key: Key) = i18n.hasKey(key)
    override fun translateKey(key: Key) = i18n.translateKey(key.withParser(key.parser ?: defaultParser))
}
