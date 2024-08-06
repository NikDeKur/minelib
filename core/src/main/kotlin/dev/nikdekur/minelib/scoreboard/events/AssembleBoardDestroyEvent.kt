package dev.nikdekur.minelib.scoreboard.events


import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
/**
 * Assemble Board Destroy Event.
 *
 * @param player who's board got destroyed.
 */

class AssembleBoardDestroyEvent(private val player: Player) : Event(), Cancellable {


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
