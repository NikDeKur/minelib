package dev.nikdekur.minelib.rpg.event

import org.bukkit.event.HandlerList
import dev.nikdekur.minelib.rpg.combat.DamageSource
import dev.nikdekur.minelib.rpg.profile.RPGProfile

class RPGKillEvent(
    profile: RPGProfile,
    val source: DamageSource
) : RPGEvent(profile) {

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}