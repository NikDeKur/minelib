package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.minelib.koin.MineLibKoinComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BukkitPlayerLocaleProvider : PlayerLocaleProvider, MineLibKoinComponent {

    override val id = "bukkit"

    override fun getLanguage(sender: CommandSender): Locale? {
        return (sender as? Player)?.let {
            val locale = it.locale
            Bukkit.getLogger().info("Locale: $locale")
            Locale.fromCode(locale)
        }
    }

}