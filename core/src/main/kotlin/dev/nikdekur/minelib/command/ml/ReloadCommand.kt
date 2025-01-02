package dev.nikdekur.minelib.command.ml

import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.command.ServiceServerCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.ext.sendSimpleMessage
import dev.nikdekur.ndkore.ext.format
import kotlinx.coroutines.runBlocking
import kotlin.time.TimeSource
import kotlin.time.measureTime

class ReloadCommand(
    override val app: PluginApplication
) : ServiceServerCommand() {
    override val name = "reload"
    override val permission = "minelib.command.reload"
    override val isConsoleFriendly = true
    override val argsRequirement = 0
    override val usageMSG = null

    override fun CommandContext.onCommand() = runBlocking {
        sendSimpleMessage("Reloading MineLib...")

        val time = TimeSource.Monotonic.measureTime {
            app.reload()
        }

        val ms = time.inWholeMilliseconds.format(2)

        sendSimpleMessage("MineLib reloaded in $ms ms")
    }
}

