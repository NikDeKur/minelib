@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.command

import dev.nikdekur.minelib.ext.sendLangMsg
import dev.nikdekur.minelib.i18n.DefaultMSG
import dev.nikdekur.minelib.i18n.MSGHolder
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.ext.copyPartialMatches
import dev.nikdekur.ndkore.ext.filterPartialMatches
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

@Suppress("unused")
abstract class ServerCommand : TabExecutor {
    private val subCommands: MutableMap<String, ServerCommand> = HashMap()
    private val subCommandAliases: MutableMap<String, ServerCommand> = HashMap()
    var commandPath = ""

    fun addSubCommands(vararg commands: ServerCommand) {
        for (command in commands) {
            subCommands[command.name] = command
            val aliases = command.aliases
            for (alias in aliases) {
                subCommandAliases[alias] = command
            }
            if (command is ServerRootCommand) {
                command.addSubCommands(*command.children)
            }
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (commandPath.isEmpty()) {
            commandPath += label.lowercase()
        }

        val permission = permission
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendLangMsg(DefaultMSG.NOT_ENOUGH_PERMISSIONS)
            return true
        }

        val isPlayer = sender is Player
        if (!isConsoleFriendly && !isPlayer) {
            sender.sendLangMsg(DefaultMSG.ONLY_FOR_PLAYERS)
            return true
        }

        if (rootCommand) {
            if (args.isEmpty()) return handleCommandResult(CommandResult.THROW_USAGE, sender)
            val arg0Lower = args[0].lowercase()
            val sub = subCommands[arg0Lower] ?: subCommandAliases[arg0Lower]
            if (sub != null) {
                commandPath += " ${sub.name}"
                return sub.onCommand(sender, cmd, label, args.copyOfRange(1, args.size))
            }
            return throwUsage(sender)
        }

        if (!commandPath.endsWith(name)) {
            commandPath += " ${this.name}"
        }

        if ((argsRequirement != null && args.size < argsRequirement!!) || (rootCommand && args.isEmpty())) {
            return throwUsage(sender)
        }

        return execute(sender, args)
    }

    private fun execute(sender: CommandSender, args: Array<String>): Boolean {
        val result: CommandResult
        val player: Player? = sender as? Player

        val execution = CommandContext(sender, args)
        if (player != null && hasCooldown() && !player.isOp) {
            val cooldown = getCooldown(player, this.commandPath)
            if (cooldown != null) {
                execution.sendCooldown(cooldown, DefaultMSG.COOLDOWN_ON_COMMAND)
                return true
            }
        }
        try {
            with(execution) {
                onCommand()
            }
        } catch (ignored: StopCommand) {
            // Do nothing
        } catch (e: Exception) {
            e.printStackTrace()
            execution.timedError(DefaultMSG.COMMAND_ERROR)
        }
        result = execution.commandResult
        return handleCommandResult(result, sender)
    }

    private fun handleCommandResult(result: CommandResult, sender: CommandSender): Boolean {
        result.execute(this, sender)
        return true
    }

    private inline fun throwUsage(sender: CommandSender): Boolean {
        handleCommandResult(CommandResult.THROW_USAGE, sender)
        return true
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<String>): List<String>? {
        val permission = permission
        if (permission != null && !sender.hasPermission(permission)) {
            return emptyList()
        }
        val arg0Lower = args[0].lowercase(Locale.getDefault())
        if (args.size > 1) {
            val sub = subCommands[arg0Lower] ?: subCommandAliases[arg0Lower]
            if (sub != null) {
                return sub.onTabComplete(sender, cmd, label, args.copyOfRange(1, args.size))
            }
        }
        val execution = CommandTabContext(sender, args)
        val completions = try {
            with(execution) {
                onTabComplete()
            }
        } catch (stop: StopCommand) {
            return emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            execution.timedError(DefaultMSG.COMMAND_TAB_ERROR)
            return emptyList()
        }

        if (completions == null && args.size == 1) {
            val matches = ArrayList<String>()
            subCommands.keys.copyPartialMatches(args.last(), matches)
            return matches
        } else completions?.filterPartialMatches(args.last())


        // Если никаких совпадений не найдено и таб комплитер полностью пустой
        // Возвращаем абсолютно пустой список.
        return completions?.ifEmpty { emptyList() } ?: emptyList()
    }

    fun hasCooldown(): Boolean {
        return cooldown > 0
    }

    abstract fun CommandContext.onCommand()
    abstract fun CommandTabContext.onTabComplete(): MutableList<String>?
    abstract val permission: String?
    abstract val isConsoleFriendly: Boolean
    abstract val argsRequirement: Int?
    abstract val usageMSG: MSGHolder?
    open fun getUsage(sender: CommandSender): String = ""
    open val aliases: Array<String> = emptyArray()
    open val cooldown: Long = 0L
    abstract val name: String

    open val rootCommand = false

    open fun register(plugin: ServerPlugin) {
        try {
            val command = plugin.getCommand(name) ?: run {
                plugin.logger.warning("Command '$name' not found. Maybe you forgot to register it?")
                return
            }
            command.executor = this
            command.tabCompleter = this

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    class StopCommand : RuntimeException()

    override fun toString(): String {
        return "ServerCommand{path=$commandPath, aliases=[${aliases.joinToString(", ") }}]}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ServerCommand
        return name == that.name && commandPath == that.commandPath && aliases.contentEquals(that.aliases)
    }

    override fun hashCode(): Int {
        return Objects.hash(name, commandPath, aliases.contentHashCode())
    }


    /**
     * Set player's cooldown for the command.
     *
     * @param player Player to set cooldown for
     * @param commandPath Path of the command
     * @param cooldown Duration of cooldown in milliseconds
     */
    abstract fun setCooldown(player: Player, commandPath: String, cooldown: Long)

    /**
     * Reset player's cooldown for the command.
     *
     * @param player Player to reset cooldown for
     * @param commandPath Path of the command
     */
    abstract fun resetCooldown(player: Player, commandPath: String)

    /**
     * Gets player's cooldown in milliseconds left.
     *
     * If a player has no cooldown, returns null.
     *
     * @param player Player to get cooldown for
     * @param commandPath Path of the command
     * @return Cooldown in milliseconds left or null if no cooldown
     */
    abstract fun getCooldown(player: Player, commandPath: String): Long?
}
