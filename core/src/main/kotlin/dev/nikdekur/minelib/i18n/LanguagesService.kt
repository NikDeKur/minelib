package dev.nikdekur.minelib.i18n

import org.bukkit.command.CommandSender

interface LanguagesService {

    fun addDataProvider(provider: LanguageDataProvider)
    fun getDataProvider(id: String): LanguageDataProvider?

    fun addLanguage(language: Language)

    operator fun get(id: Language.Code): Language?

    fun getLanguage(sender: CommandSender): Language
    fun setLanguage(sender: CommandSender, language: Language)
}