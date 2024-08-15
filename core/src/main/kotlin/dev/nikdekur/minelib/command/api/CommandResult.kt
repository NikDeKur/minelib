package dev.nikdekur.minelib.command.api

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
        @JvmField
        val EMPTY = CommandResult()

        @JvmField
        val SUCCESS = CommandResult(CommandAction.Default.SET_COOLDOWN)

        @JvmField
        val SUCCESS_NO_COOLDOWN = EMPTY

        @JvmField
        val THROW_USAGE = CommandResult(CommandAction.Default.THROW_USAGE)
    }
}
