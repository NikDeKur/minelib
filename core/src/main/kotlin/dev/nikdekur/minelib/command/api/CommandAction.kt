package dev.nikdekur.minelib.command.api

import dev.nikdekur.minelib.ext.sendLangMsg
import dev.nikdekur.minelib.ext.sendSimpleMessage
import dev.nikdekur.ndkore.ext.isBlankOrEmpty
import dev.nikdekur.ndkore.`interface`.Snowflake
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface CommandAction : Snowflake<String> {

    fun execute(command: ServerCommand, sender: CommandSender)

    enum class Default : CommandAction {
        THROW_USAGE,
        SET_COOLDOWN,
        UNSET_COOLDOWN;

        override val id: String = name.lowercase().replace("_", "-")

        override fun execute(command: ServerCommand, sender: CommandSender) {
            val player = sender as? Player
            when (this) {
                THROW_USAGE -> {
                    val usageMSG = command.usageMSG
                    if (usageMSG != null) {
                        sender.sendLangMsg(usageMSG)
                        return
                    }

                    val usageStr = command.getUsage(sender)
                    if (!usageStr.isBlankOrEmpty()) {
                        sender.sendSimpleMessage(usageStr)
                        return
                    }

                    throw UnsupportedOperationException("Usage is not defined for command $command")
                }

                SET_COOLDOWN -> {
                    if (command.hasCooldown() && player != null) {
                        command.service.setCooldown(player, command, command.cooldown)
                    }
                }

                UNSET_COOLDOWN -> {
                    if (command.hasCooldown() && player != null) {
                        command.service.resetCooldown(player, command)
                    }
                }

            }
        }
    }
}
