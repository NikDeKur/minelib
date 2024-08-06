@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.ext

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.plugin.PluginManager

/**
 * Cancel an event
 *
 * Equivalent to [Cancellable.setCancelled]``` = true```
 */
inline fun Cancellable.cancel() {
    isCancelled = true
}

/**
 * Call an event
 *
 * Use [PluginManager.callEvent] to call an event
 */
inline fun Event.call() {
    Bukkit.getPluginManager().callEvent(this)
}

/**
 * Call an event and return whether it is cancelled
 *
 * @see [Event.call]
 */
inline fun <T> T.callEventIsCancelled(): Boolean where T : Event, T : Cancellable {
    return apply(Event::call).isCancelled
}