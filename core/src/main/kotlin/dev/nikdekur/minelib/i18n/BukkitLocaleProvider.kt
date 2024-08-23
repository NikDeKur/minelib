package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.i18n.locale.Locale
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BukkitLocaleProvider : PlayerLocaleProvider {

    override val id = "bukkit"

    override fun getLocale(sender: CommandSender): Locale? {
        return (sender as? Player)?.let {
            val locale = it.locale
            Locale.fromCode(locale)
        }
    }

}