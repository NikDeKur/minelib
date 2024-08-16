package dev.nikdekur.minelib.command.ml

import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.command.ServiceServerCommand
import dev.nikdekur.minelib.command.api.CommandContext
import dev.nikdekur.minelib.ext.sendSimpleMessage
import dev.nikdekur.minelib.i18n.msg.MSGHolder
import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.ndkore.ext.format
import kotlin.system.measureNanoTime

class ReloadCommand(
    override val app: ServerPlugin
) : ServiceServerCommand() {
    override val name = "reload"
    override val permission = "minelib.command.reload"
    override val isConsoleFriendly = true
    override val argsRequirement = 0
    override val usageMSG: MSGHolder? = null

    override fun CommandContext.onCommand() {
        sendSimpleMessage("Reloading MineLib...")

        val time = measureNanoTime {
            MineLib.instance.reload()
        }
        val ms = (time / 1_000_000.0).format(2)

        sendSimpleMessage("MineLib reloaded in $ms ms")
    }
}

