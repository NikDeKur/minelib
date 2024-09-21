@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.utils

import dev.nikdekur.minelib.ext.sendSimpleMessage
import dev.nikdekur.ndkore.ext.format
import dev.nikdekur.ndkore.ext.measureAverageTime
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.util.Vector
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.measureTimedValue

fun debug(vararg messageParts: Any?) {
    val server = Bukkit.getServer()
    val msg = messageParts.joinToString(" | ") { it.toString() }

    if (server == null) {
        println("[DEBUG] $msg")
        return
    }

    Bukkit.getOnlinePlayers().forEach {
        if (!it.isOp) return
        sendDebug(it, msg)
    }
    sendDebug(Bukkit.getConsoleSender(), msg)
}

inline fun sendDebug(sender: CommandSender, message: String) {
    sender.sendSimpleMessage("§c[DEBUG] §f$message")
}


inline fun Duration.format(precision: Int = 6) = inWholeMilliseconds.format(precision)


inline fun <T : Any> TimeSource.debugExecTime(message: String, code: () -> T): T {
    val data = measureTimedValue {
        code()
    }
    debug("$message: ${data.duration.format()} ms")
    return data.value
}

inline fun <T : Any> TimeSource.debugExecTime(code: () -> T) = debugExecTime("Execution time", code)

inline fun TimeSource.debugAverageExecTime(message: String, times: Int, code: (Int) -> Unit) {
    val time = measureAverageTime(times, code)
    debug("$message: ${time.format()} ms")
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
val zeroLocation by lazy {
    Location(null, 0.0, 0.0, 0.0)
}

val zeroVector by lazy {
    Vector(0, 0, 0)
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