@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.command

import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.command.api.CommandResult
import dev.nikdekur.minelib.command.api.CommandService
import dev.nikdekur.minelib.command.api.CommandTabContext
import dev.nikdekur.minelib.command.api.ServerCommand
import dev.nikdekur.minelib.command.api.ServerCommand.StopCommand
import dev.nikdekur.minelib.ext.sendLangMsg
import dev.nikdekur.minelib.i18n.msg.DefaultMSG
import dev.nikdekur.ndkore.ext.filterPartialMatches
import dev.nikdekur.ndkore.service.inject
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import kotlin.time.Duration

@Suppress("unused")
abstract class ServiceServerCommand : ServerCommand {

    override val service: CommandService by inject()

    var commandPath = ""



    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (commandPath.isEmpty()) {
            commandPath += label.lowercase()
        }

        val permission = permission
        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendLangMsg(DefaultMSG.NOT_ENOUGH_PERMISSIONS_CMD, "permission" to permission)
            return true
        }

        val isPlayer = sender is Player
        if (!isConsoleFriendly && !isPlayer) {
            sender.sendLangMsg(DefaultMSG.ONLY_FOR_PLAYERS)
            return true
        }

        if (!commandPath.endsWith(name)) {
            commandPath += " ${this.name}"
        }

        if (argsRequirement != null && args.size < argsRequirement!!) {
            return throwUsage(sender)
        }

        return execute(sender, args)
    }

    private fun execute(sender: CommandSender, args: Array<String>): Boolean {
        val result: CommandResult
        val player: Player? = sender as? Player

        val execution = CommandContext(sender, args)
        if (player != null && cooldown > Duration.ZERO && !player.isOp) {
            val cooldown = service.getCooldown(player, this)
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
        handleCommandResult(CommandResult.Companion.THROW_USAGE, sender)
        return true
    }

    override fun onTabComplete(sender: CommandSender, cmd: Command, label: String, args: Array<String>): List<String> {
        val permission = permission
        if (permission != null && !sender.hasPermission(permission))
            return emptyList()

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

        completions?.filterPartialMatches(args.last())

        // If no completions are found, return an empty list
        return completions?.ifEmpty { emptyList() } ?: emptyList()
    }




    override fun toString(): String {
        return "ServerCommand{path=$commandPath, aliases=[${aliases.joinToString(", ") }}]}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ServiceServerCommand
        return name == that.name && commandPath == that.commandPath && aliases.contentEquals(that.aliases)
    }

    override fun hashCode(): Int {
        return Objects.hash(name, commandPath, aliases.contentHashCode())
    }
}
