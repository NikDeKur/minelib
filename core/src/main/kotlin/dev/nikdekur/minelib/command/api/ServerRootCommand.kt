package dev.nikdekur.minelib.command.api

import dev.nikdekur.minelib.command.ServiceServerCommand

interface ServerRootCommand : ServerCommand {

    val subCommands: Collection<ServiceServerCommand>

    fun addSubCommand(command: ServiceServerCommand)
}