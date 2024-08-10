package dev.nikdekur.minelib.command

import dev.nikdekur.minelib.plugin.ServerPlugin

abstract class ServiceServiceRootCommand : ServiceServerCommand() {

    override fun CommandContext.onCommand() {
        // Argument requirement is 1, so it can't be called without arguments
        // This argument would be parsed as subcommand name.
        // If no subcommand is found, the usage message will be sent.
        // So there is no way this block will be executed.
    }
    override fun CommandTabContext.onTabComplete(): MutableList<String>? { return null }

    override val argsRequirement: Int? = 1
    val children: Iterable<ServiceServerCommand> = emptyList()

    override fun addSubCommand(command: ServiceServerCommand) {
        super.addSubCommand(command)

        children.forEach(command::addSubCommand)
    }

    override fun register(plugin: ServerPlugin) {
        super.register(plugin)
        children.forEach(::addSubCommand)
    }

}