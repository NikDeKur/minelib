package dev.nikdekur.minelib.i18n

import dev.nikdekur.ndkore.`interface`.Snowflake
import org.bukkit.command.CommandSender

interface LanguageDataProvider : Snowflake<String> {

    fun getLanguage(sender: CommandSender): Language?
    fun setLanguage(sender: CommandSender, language: Language)
}