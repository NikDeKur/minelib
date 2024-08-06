package dev.nikdekur.minelib.command

import org.bukkit.command.CommandSender

class CommandResult : Iterable<CommandAction> {
    private val actions: MutableList<CommandAction> = ArrayList()

    constructor(actions: List<CommandAction>) {
        this.actions.addAll(actions)
    }

    constructor(vararg actions: CommandAction) {
        this.actions.addAll(actions.toList())
    }

    override fun iterator(): MutableIterator<CommandAction> {
        return actions.iterator()
    }

    fun getActions(): List<CommandAction> {
        return actions
    }

    fun execute(command: ServerCommand, sender: CommandSender) {
        actions.forEach { it.execute(command, sender) }
    }

    companion object {
        @JvmStatic
        val EMPTY = CommandResult()

        @JvmStatic
        val SUCCESS = CommandResult(CommandAction.Default.SET_COOLDOWN)

        @JvmStatic
        val SUCCESS_NO_COOLDOWN = EMPTY

        @JvmStatic
        val THROW_USAGE = CommandResult(CommandAction.Default.THROW_USAGE)
    }
}
