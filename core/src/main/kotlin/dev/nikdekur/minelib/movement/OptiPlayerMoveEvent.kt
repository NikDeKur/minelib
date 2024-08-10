package dev.nikdekur.minelib.movement

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

data class OptiPlayerMoveEvent(
    val player: Player,
    val from: Location,
    val to: Location,
) : Event(true), Cancellable {


    override fun getHandlers(): HandlerList {
        return Companion.handlers
    }

    companion object {
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    var cancel = false
    var cancelYaw = false
    var cancelPitch = false

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    override fun isCancelled(): Boolean {
        return cancel
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     * @param yaw true if you wish to cancel the yaw
     * @param pitch true if you wish to cancel the pitch
     */
    override fun setCancelled(cancel: Boolean) = setCancelled(cancel, yaw = true, pitch = true)

    /**
     * Sets the cancellation state of this event
     *
     * @param cancel true if you wish to cancel this event
     * @param yaw true if you wish to cancel the yaw
     * @param pitch true if you wish to cancel the pitch
     */
    fun setCancelled(cancel: Boolean, yaw: Boolean, pitch: Boolean) {
        this.cancel = cancel
        this.cancelYaw = yaw
        this.cancelPitch = pitch
    }
}