package dev.nikdekur.minelib.command.api

import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.koin.MineLibKoinComponent
import dev.nikdekur.minelib.plugin.ServerPlugin
import org.bukkit.command.CommandSender
import kotlin.time.Duration

interface ServerCommand : MineLibKoinComponent {

    val app: ServerPlugin

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
    val usageMSG: MSGHolder?


    fun CommandContext.onCommand()
    fun CommandTabContext.onTabComplete(): MutableList<String>? = null
    fun getUsage(sender: CommandSender): String = ""

    fun register(plugin: ServerPlugin)


    class StopCommand : RuntimeException()
}