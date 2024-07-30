package dev.nikdekur.minelib.rpg.event

import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.stat.RPGStat

open class RPGStatChangeEvent<T : Comparable<T>>(
    profile: RPGProfile,
    val stat: RPGStat<T>,
    val oldValue: T,
    var newValue: T
) : RPGEvent(profile), Cancellable {

    inline val isIncrease
        get() = newValue > oldValue



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