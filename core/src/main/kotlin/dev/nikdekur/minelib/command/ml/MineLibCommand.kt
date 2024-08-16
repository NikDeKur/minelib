package dev.nikdekur.minelib.command.ml

import dev.nikdekur.minelib.command.ServiceServerRootCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.i18n.msg.DefaultMSG
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.plugin.ServerPlugin

class MineLibCommand(
    override val app: ServerPlugin
): ServiceServerRootCommand() {

    override val name = "minelib"
    override val permission = "minelib.command"
    override val isConsoleFriendly = true
    override val argsRequirement = 0
    override val usageMSG: MSGHolder? = null

    init {
        addSubCommand(ReloadCommand(app))
    }

    override fun CommandContext.onCommand() {
        send(
            DefaultMSG.CMD_MINELIB_INFO,

            "version" to app.description.version,
            "commands" to subCommands.size,
            "listeners" to app.listeners.size,
            "services" to app.servicesManager.services.size,
            "uptime" to app.uptime.toString()
        )
    }
}