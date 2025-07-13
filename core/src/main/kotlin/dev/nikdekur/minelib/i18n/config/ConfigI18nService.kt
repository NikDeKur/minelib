@file:Suppress("NOTHING_TO_INLINE")
@file:OptIn(UnsafeReflectAPI::class)

package dev.nikdekur.minelib.i18n.config

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.config.CustomYamlRepresenter
import dev.nikdekur.minelib.config.SmartYamlConfiguration
import dev.nikdekur.minelib.i18n.BukkitLocaleProvider
import dev.nikdekur.minelib.i18n.I18nService
import dev.nikdekur.minelib.i18n.PlayerLocaleProvider
import dev.nikdekur.minelib.i18n.YamlBundleUtils.EMPTY_CHAR
import dev.nikdekur.minelib.i18n.locale.LocaleConfig
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.ndkore.ext.addById
import dev.nikdekur.ndkore.map.MultiMap
import dev.nikdekur.ndkore.placeholder.PatternPlaceholderParser
import dev.nikdekur.ndkore.reflect.JVMReflectMethod
import dev.nikdekur.ndkore.reflect.UnsafeReflectAPI
import dev.nikdekur.ndkore.service.Service
import dev.nikdekur.ndkore.service.dependencies
import dev.nikdekur.ndkore.service.inject
import dev.nikdekur.ornament.dataset.DataSetService
import dev.nikdekur.ornament.dataset.get
import dev.nikdekur.ornament.i18n.Key
import dev.nikdekur.ornament.i18n.Locale
import dev.nikdekur.ornament.i18n.toLanguageTag
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.yaml.snakeyaml.DumperOptions
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
        translations: MultiMap<Locale, String, String>,
        merge: Boolean
    ) {
        val bundleName = id.lowercase()
        val bundleDir = File(i18nDir, bundleName)
        bundleDir.mkdirs()

        // Save the translations
        translations.forEach { locale, translations ->
            val code = locale.toLanguageTag()
            val langFile = File(bundleDir, "${code}.yml")
            langFile.createNewFile() // Making sure the file exists.

            val representer = CustomYamlRepresenter()
            val dumperOptions = DumperOptions().apply {
                splitLines = false
            }

            val langConfig = SmartYamlConfiguration(
                yamlOptions = dumperOptions,
                yamlRepresenter = representer
            )
            langConfig.options().pathSeparator(EMPTY_CHAR)


            if (merge) {
                langConfig.load(langFile)
            }


            // Check if there are any missing translations
            val anyMissed = translations.keys.any { it !in langConfig }

            // If there are no missing translations, skip this locale
            if (!anyMissed) return@forEach

            // If some translations are missing, recreate config to add them (in right order)
            // Fill the new config with all translations with priority to the existing ones
            mergeYaml(translations, langConfig)

            // Save the new config
            langConfig.save(langFile)
        }

        (i18nDataset as Service).reload()
    }

    fun mergeYaml(
        source: Map<String, String>,
        with: ConfigurationSection
    ) {
        source.forEach { (key, requiredValue) ->
            if (!with.contains(key)) {
                with[key] = valueToProperType(requiredValue)
            }
        }
    }

    fun valueToProperType(value: Any): Any {
        return when (value) {
            is String -> if (value.contains("\n")) value.split("\n") else value
            is Collection<*> -> value
            else -> value.toString()
        }
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

//        .also {
//        val service = i18nDataset as YamlKtMultiFileDataSetService<*>
//        println(service.delegate.map)
//        val bundle = (key.bundle ?: Bundle.Default).name
//        println("Bundle: $bundle")
//        val bundleSection = service.getSection(bundle)
//        println("Found: $bundleSection")
//        val languageTag = (key.locale ?: defaultLocale).toLanguageTag()
//        println("Locale: $languageTag")
//        val localeSection = bundleSection?.getSection(languageTag)
//        println("Locale Section: $localeSection")
//        val keyValue = localeSection?.get<String>(key.key)
//        println("Key value: $keyValue")
//        println("Requested translation for key: $key -> $it")
//    }
}
