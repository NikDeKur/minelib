@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.MineLibModule
import dev.nikdekur.minelib.ext.bLogger
import dev.nikdekur.minelib.ext.pairs
import dev.nikdekur.ndkore.ext.addById
import dev.nikdekur.ndkore.ext.map
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigLanguagesService(override val app: MineLib) : LanguagesService, MineLibModule {
    val languages = HashMap<Language.Code, Language>()

    val defaultDataProvider = BukkitLanguageDataProvider

    var dataProviderId: String? = null
    private var _dataProvider: LanguageDataProvider? = null
    val dataProvider: LanguageDataProvider
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

    lateinit var defaultLang: Language
    lateinit var defaultLangCode: Language.Code

    val dataProviders = HashMap<String, LanguageDataProvider>()

    val messages = HashMap<String, MSGHolder>()
    val defaultMessages = HashMap<String, String>()



    override fun onLoad() {
        importEnumMessages(DefaultMSG::class.java)

        // Go back from plugin folder to the server folder
        val containerDir = app.dataFolder.parentFile.parentFile
        val languagesDir = File(containerDir, "languages")
        val config = app.loadConfig<LanguagesConfig>("config", languagesDir)

        config.dataProvider?.let {
            dataProviderId = it
        }

        val files = languagesDir.listFiles {
            it.extension == "yml"
        } ?: emptyArray()

        files.forEach(::loadLanguage)


        // Add the default english language if it's enabled or no languages loaded
        val enableDefaultEnglish = config.enableDefaultEnglish || languages.isEmpty()
        if (enableDefaultEnglish)
            addLanguage(defaultEnglish)

        // Get the default language code and if language not found, use the first found language code.
        val defaultLangCodeStr = config.defaultLanguage
        var defaultLangCode = defaultLangCodeStr?.let(Language.Code::fromCode)
        if (defaultLangCode == null) {
            defaultLangCode = languages.keys.first()
            if (defaultLangCodeStr == null)
                bLogger.warning("Default language code not specified in settings! Using '$defaultLangCode'!")
            else
                bLogger.warning("Default language code specified in settings ($defaultLangCodeStr is not correct) Example of correct: en_us, ru_ru! Using '$defaultLangCode'!")
        }

        // Set default language to language found by code set
        // If language by code not found (could occur only with user code), report and use the first language found
        val defLangTemp = languages[defaultLangCode]
        if (defLangTemp == null) {
            defaultLang = languages.values.first()
            bLogger.warning("Selected default language '$defaultLangCodeStr' not found! Using '${defaultLang.code}'!")
        } else {
            defaultLang = defLangTemp
        }
        this.defaultLangCode = defaultLang.code
    }

    fun loadLanguage(file: File) {
        val fileName = file.name
        if (fileName == "config.yml") return
        if (!fileName.endsWith(".yml")) return
        val code = Language.Code.fromFileName(fileName) ?: return
        val langConfig: YamlConfiguration = YamlConfiguration.loadConfiguration(file)
        val messagesMap = langConfig.pairs.toMutableMap()
            .map({it.key}, {
                return@map when (val o = it.value) {
                    is String -> o
                    is List<*> -> o.joinToString("\n")
                    else -> null
                }
            })
        val language = StringLanguage(code, messagesMap)
        addLanguage(language)
    }

    override fun onUnload() {
        languages.clear()
    }


    override fun addDataProvider(provider: LanguageDataProvider) {
        dataProviders.addById(provider)
    }

    override fun getDataProvider(id: String): LanguageDataProvider? {
        return dataProviders[id]
    }


    override fun addLanguage(language: Language) {
        languages[language.code] = language
    }

    fun getLanguage(code: Language.Code): Language? {
        return languages[code]
    }







    val languageCodes: Set<Language.Code>
        get() = languages.keys

    val languageObjects: Collection<Language>
        get() = languages.values

    override operator fun get(id: Language.Code): Language? {
        return languages[id]
    }


    fun addMessage(holder: MSGHolder) {
        messages.addById(holder)
        defaultMessages[holder.id] = holder.defaultText
    }
    fun hasDefaultMessage(id: String): Boolean {
        return defaultMessages.containsKey(id)
    }
    fun getMessage(id: String): MSGHolder? {
        return messages[id]
    }

    /**
     * Get the default message by id
     *
     * If the message is not found, it will return the id
     *
     * @param id id of the message
     * @return The message or the id if not found
     */
    fun getDefaultMessage(id: String): String {
        return defaultMessages[id] ?: id
    }

    override fun getLanguage(sender: CommandSender): Language {
        return dataProvider.getLanguage(sender) ?: defaultLang
    }

    override fun setLanguage(sender: CommandSender, language: Language) {
        dataProvider.setLanguage(sender, language)
    }

    fun <E : Enum<out MSGHolder>> importEnumMessages(enum: Class<E>) {
        require(enum.isEnum) { "importFromEnum function can only import messages from enum" }
        @Suppress("UNCHECKED_CAST")
        val entries = enum.enumConstants as Array<out MSGHolder>
        entries.forEach(::addMessage)
    }

    val defaultEnglish = StringLanguage(
        Language.Code.EN_US,
        defaultMessages
    )
}
