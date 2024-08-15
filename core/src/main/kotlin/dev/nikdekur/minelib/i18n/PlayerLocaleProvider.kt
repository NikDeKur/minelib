package dev.nikdekur.minelib.i18n

import dev.nikdekur.minelib.i18n.locale.Locale
import dev.nikdekur.ndkore.`interface`.Snowflake
import org.bukkit.command.CommandSender

interface PlayerLocaleProvider : Snowflake<String> {
    fun getLanguage(sender: CommandSender): Locale?
}