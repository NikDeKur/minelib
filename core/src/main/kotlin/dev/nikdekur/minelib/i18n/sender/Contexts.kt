package dev.nikdekur.minelib.i18n.sender

import dev.nikdekur.minelib.i18n.locale.LocaleProvider
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface SenderContext {
    val sender: CommandSender
    val localeProvider: LocaleProvider
}

data class SimpleSenderContext(
    override val sender: CommandSender,
    override val localeProvider: LocaleProvider
) : SenderContext


interface PlayerContext : SenderContext {
    override val sender: Player
}

data class SimplePlayerContext(
    override val sender: Player,
    override val localeProvider: LocaleProvider
) : PlayerContext