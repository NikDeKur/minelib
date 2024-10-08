package dev.nikdekur.minelib.command

import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.CommandTabContext
import dev.nikdekur.minelib.command.api.RootServerCommand
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.ext.copyPartialMatches
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.util.LinkedList

abstract class ServiceServerRootCommand : ServiceServerCommand(), RootServerCommand {

    private val subCommandsMap = HashMap<String, ServiceServerCommand>()

    override val subCommands
        get() = subCommandsMap.values

    override fun addSubCommand(command: ServiceServerCommand) {
        subCommandsMap[command.name] = command
        val aliases = command.aliases
        for (alias in aliases) {
            subCommandsMap[alias] = command
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (args.isNotEmpty()) {
            val arg0Lower = args[0].lowercase()
            val sub = subCommandsMap[arg0Lower]
            if (sub != null) {
                commandPath += " ${sub.name}"
                return sub.onCommand(sender, cmd, label, args.copyOfRange(1, args.size))
            }

            // If no subcommand is found or the command is called without any arguments,
            // then onCommand is called
        }

        return super.onCommand(sender, cmd, label, args)
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<String>): List<String> {
        if (args.isNotEmpty()) {
            val arg0Lower = args[0].lowercase()
            val sub = subCommandsMap[arg0Lower]
            if (sub != null)
                return sub.onTabComplete(sender, cmd, label, args.copyOfRange(1, args.size))
        }

        val completions = super<ServiceServerCommand>.onTabComplete(sender, cmd, label, args)
        if (completions.isEmpty() && args.isNotEmpty())
            return LinkedList<String>().copyPartialMatches(args[0], subCommandsMap.keys)
        return completions
    }

    override fun CommandContext.onCommand() {
        // By default, throw usage
        // But user can override this method to do something else
        throwUsage()
    }

    override fun CommandTabContext.onTabComplete(): MutableList<String>? { return null }

    val children: Iterable<ServiceServerCommand> = emptyList()

    override fun register(plugin: ServerPlugin) {
        super.register(plugin)
        children.forEach {
            it.register(plugin)
        }
    }

}