package dev.nikdekur.minelib.i18n

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BukkitLanguageDataProvider : LanguageDataProvider {
    override val id = "bukkit"

    override fun getLanguage(sender: CommandSender): Language {
        return if (sender is Player) {
            val code = Language.Code.fromCodeOrThrow(sender.locale)
            LanguagesManager.getLanguage(code) ?: LanguagesManager.defaultLang
        } else
            LanguagesManager.defaultLang
    }

    override fun setLanguage(sender: CommandSender, language: Language) {
        // Not supported
    }

}