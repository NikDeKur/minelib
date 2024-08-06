package dev.nikdekur.minelib.rpg.event

import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import dev.nikdekur.minelib.rpg.combat.DamageSource
import dev.nikdekur.minelib.rpg.profile.RPGProfile

/**
 * Event called when a profile is going to be damaged.
 *
 * This event is called before the damage is applied to the profile and after damage is decreased depends on stats.
 *
 * @param profile The profile that is going to be damaged
 * @param source The source of the damage
 * @param damage The scaled damage that is going to be applied
 */
class RPGDamageEvent(
    profile: RPGProfile,
    val source: DamageSource,
    var damage: Double
) : RPGEvent(profile), Cancellable {

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

    var cancel = false
    override fun isCancelled(): Boolean {
        return cancel
    }

    override fun setCancelled(cancel: Boolean) {
        this.cancel = cancel
    }
}