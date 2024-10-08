package dev.nikdekur.minelib.command.api

import dev.nikdekur.minelib.i18n.msg.MessageReference
import dev.nikdekur.minelib.service.PluginComponent
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import kotlin.time.Duration

interface ServerCommand : TabExecutor, PluginComponent {

    val service: CommandService

    val name: String
    val aliases: Array<String>
        get() = emptyArray()

    val cooldown: Duration
        get() = Duration.ZERO

    fun hasCooldown(): Boolean = cooldown > Duration.ZERO

    val permission: String?
    val isConsoleFriendly: Boolean
    val argsRequirement: Int?
    val usageMSG: MessageReference?


    fun CommandContext.onCommand()
    fun CommandTabContext.onTabComplete(): MutableList<String>? = null
    fun getUsage(sender: CommandSender): String = ""

    class StopCommand : RuntimeException()
}