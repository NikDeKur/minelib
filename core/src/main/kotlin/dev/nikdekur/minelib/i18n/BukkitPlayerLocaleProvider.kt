package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.i18n.locale.Locale
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BukkitPlayerLocaleProvider : PlayerLocaleProvider {

    override val id = "bukkit"

    override fun getLanguage(sender: CommandSender): Locale? {
        return (sender as? Player)?.let {
            val locale = it.locale
            Bukkit.getLogger().info("Locale: $locale")
            Locale.fromCode(locale)
        }
    }

}