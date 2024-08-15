@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.PluginService
import dev.nikdekur.minelib.ext.pairs
import dev.nikdekur.minelib.i18n.bundle.Bundle
import dev.nikdekur.minelib.i18n.bundle.ConfigBundle
import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.i18n.locale.LocaleConfig
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.ext.addById
import dev.nikdekur.ndkore.map.multi.MultiHashMap
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import kotlin.reflect.KClass

class ConfigLanguagesService(override val app: ServerPlugin) : LanguagesService, PluginService {

    override val bindClass: KClass<*>
        get() = LanguagesService::class

    val bundles = HashMap<String, Bundle>()

    val defaultDataProvider = BukkitPlayerLocaleProvider

    var dataProviderId: String? = null
    private var _dataProvider: PlayerLocaleProvider? = null
    val dataProvider: PlayerLocaleProvider
        get() = _dataProvider ?: run {
            if (dataProviderId == null) {
                app.logger.severe("Language data provider not found! Using $defaultDataProvider!")
                _dataProvider = defaultDataProvider
                return defaultDataProvider
            }

            val provider = dataProviders[dataProviderId]
            _dataProvider = provider
            provider ?: run {
                app.logger.warning("Language data provider '$dataProviderId' not found! Using $defaultDataProvider!")
                defaultDataProvider
            }
        }

    lateinit var defaultLang: Locale
    lateinit var defaultLangCode: Locale

    val dataProviders = HashMap<String, PlayerLocaleProvider>()

    val messages = MultiHashMap<Locale, String, String>()



    override fun onLoad() {
        // Go back from plugin folder to the server folder
        val containerDir = app.dataFolder.parentFile.parentFile
        val i18nDir = File(containerDir, "i18n")
        val config = app.loadConfig<LocaleConfig>("config", i18nDir)


        addDataProvider(BukkitPlayerLocaleProvider)

        dataProviderId = config.dataProvider

        val bundlesDir = File(i18nDir, "bundles")

        val bundlesDirs = bundlesDir.listFiles {
            it.isDirectory
        } ?: emptyArray()

        bundlesDirs.forEach(::loadBundle)


        // Get the default language code and if language not found, use the first found language code.
        val defaultLangCodeStr = config.defaultLocale
        this.defaultLangCode = defaultLangCodeStr.let(Locale::fromCodeOrThrow)
    }

    fun loadBundle(file: File) {
        val files = file.listFiles {
            it.extension == "yml"
        } ?: emptyArray()

        files.forEach {
            val locale = Locale.fromFileName(it.name) ?: return
            val langConfig: YamlConfiguration = YamlConfiguration.loadConfiguration(file)

            langConfig.pairs.forEach {
                val key = it.key
                val message = when (val o = it.value) {
                    is String -> o
                    is List<*> -> o.joinToString("\n")
                    else -> null
                } ?: return@forEach
                messages.put(locale, key, message)
            }
        }
    }

    override fun onUnload() {
        bundles.clear()
        messages.clear()
    }




    override fun addDataProvider(provider: PlayerLocaleProvider) {
        dataProviders.addById(provider)
    }

    override fun getDataProvider(id: String): PlayerLocaleProvider? {
        return dataProviders[id]
    }





    override fun newBundle(id: String, messages: Collection<MSGHolder>) {
        bundles[id] = ConfigBundle(id, messages, this.messages)
    }

    override fun getBundle(id: String): Bundle? {
        return bundles[id]
    }



    override fun getLanguage(sender: CommandSender): Locale {
        return dataProvider.getLanguage(sender) ?: defaultLang
    }

}
