package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.i18n.BukkitLanguageDataProvider.languageService
import dev.nikdekur.minelib.koin.MineLibKoinComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.inject

object BukkitLanguageDataProvider : LanguageDataProvider, MineLibKoinComponent {

    val languageService: LanguagesService by inject()

    override val id = "bukkit"

    override fun getLanguage(sender: CommandSender): Language? {
        return (sender as? Player)?.let {
            val code = Language.Code.fromCodeOrThrow(sender.locale)
            languageService[code]
        }
    }

    override fun setLanguage(sender: CommandSender, language: Language) {
        // Not supported
    }

}