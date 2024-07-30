package dev.nikdekur.minelib.scoreboard.events

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import dev.nikdekur.minelib.scoreboard.AssembleBoard
/**
 * Assemble Board Created Event.
 *
 * @param board of player.
 */
class AssembleBoardCreatedEvent(private val board: AssembleBoard) : Event(), Cancellable {

    override fun getHandlers() = HANDLERS
    companion object {
        private val HANDLERS = HandlerList()
        @JvmStatic
        fun getHandlerList() = HANDLERS
    }


    var cancel = false
    override fun isCancelled(): Boolean {
        return cancel
    }

    override fun setCancelled(cancel: Boolean) {
        this.cancel = cancel
    }
}
