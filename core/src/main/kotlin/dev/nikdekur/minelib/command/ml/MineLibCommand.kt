package dev.nikdekur.minelib.command.ml

import dev.nikdekur.minelib.command.ServiceServerRootCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.RootServerCommand
import dev.nikdekur.minelib.command.api.ServerCommand
import dev.nikdekur.minelib.i18n.msg.DefaultMSG
import dev.nikdekur.minelib.plugin.ServerPlugin
import java.util.LinkedList

class MineLibCommand(
    override val app: ServerPlugin
): ServiceServerRootCommand() {

    override val name = "minelib"
    override val permission = "minelib.command"
    override val isConsoleFriendly = true
    override val argsRequirement = 0
    override val usageMSG = DefaultMSG.CMD_MINELIB_USAGE

    init {
        addSubCommand(ReloadCommand(app))
    }

    fun collectAllCommands(command: ServerCommand): List<ServerCommand> {
        val allCommands = LinkedList<ServerCommand>()
        allCommands.add(command)
        if (command is RootServerCommand) {
            command.subCommands.forEach { subCommand ->
                allCommands.addAll(collectAllCommands(subCommand))
            }
        }
        return allCommands
    }

    override fun CommandContext.onCommand() {
        val commands = app.components
            .filterIsInstance<ServerCommand>()
            .flatMap(::collectAllCommands)

        send(
            DefaultMSG.CMD_MINELIB_INFO,

            "version" to app.description.version,
            "commands" to commands.size,
            "listeners" to app.listeners.size,
            "services" to app.servicesManager.services.size,
            "uptime" to app.uptime.toString()
        )
    }


}