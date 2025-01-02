package dev.nikdekur.minelib.i18n

import dev.nikdekur.ndkore.`interface`.Unique
import dev.nikdekur.ornament.i18n.Locale
import org.bukkit.command.CommandSender

interface PlayerLocaleProvider : Unique<String> {
    fun getLocale(sender: CommandSender): Locale?
}