@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.utils

import dev.nikdekur.minelib.ext.nanosToMs
import dev.nikdekur.minelib.ext.sendSimpleMessage
import dev.nikdekur.ndkore.ext.format
import dev.nikdekur.ndkore.ext.measureAverageTime
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.util.Vector

object Utils {

    fun debug(vararg messageParts: Any?) {
        val msg = messageParts.joinToString(" | ") { it.toString() }
        Bukkit.getOnlinePlayers().forEach {
            if (!it.isOp) return
            sendDebug(it, msg)
        }
        sendDebug(Bukkit.getConsoleSender(), msg)
    }

    inline fun sendDebug(sender: CommandSender, message: String) {
        sender.sendSimpleMessage("§c[DEBUG] §f$message")
    }


    inline fun <T : Any> debugExecTime(message: String, code: () -> T): T {
        val time = System.nanoTime()
        val r = code()
        val finish = System.nanoTime()
        debug("$message: ${(finish - time).nanosToMs().format(6)} ms")
        return r
    }

    inline fun <T : Any> debugExecTime(code: () -> T) = debugExecTime("Execution time", code)


    inline fun debugAverageExecTime(message: String, times: Int, code: (Int) -> Unit) {
        val time = measureAverageTime(times, code)
        debug("$message: ${time.nanosToMs().format(6)} ms")
    }


    inline fun applyColors(text: String): String {
        return ChatColor.translateAlternateColorCodes('&', text)
    }




    fun processResultableCommand(command: String, hide: Boolean = false): String {
        val console = UpdatedConsoleCommandSender(Bukkit.getConsoleSender(), hide)
        Bukkit.dispatchCommand(console, command)
        return console.output
    }

    /**
     * Get the zero location.
     *
     * Returns the location with the world set to null and the coordinates set to 0.
     */
    val zeroLocation = Location(null, 0.0, 0.0, 0.0)

    val zeroVector = Vector(0, 0, 0)
}

/**
 * Check if the current thread is the main thread.
 *
 * @return `true` if the current thread is the main thread, `false` otherwise
 */
val isPrimaryThread: Boolean
    get() = Bukkit.isPrimaryThread()


inline fun requireMainThread(operation: String) {
    require(isPrimaryThread) { "Operation '$operation' must be called from the main thread!" }
}