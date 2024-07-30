package dev.nikdekur.minelib.rpg.combat

import dev.nikdekur.minelib.rpg.profile.RPGProfile
import java.util.UUID

/**
 * Represents a combat tracker that tracks combat time of living objects.
 *
 * This class is used to track the combat time of living objects.
 * It is used to determine if a player is in combat or not.
 *
 * @param rpg Player RPG profile
 * @param combatDelay Combat Delay in ms
 */
class CombatTracker(val combatDelay: Long) {

    val combatTimeMap = LinkedHashMap<UUID, Long>()

    val combats: Set<UUID>
        get() = combatTimeMap.keys

    var combatStartTime = 0L
    val isInCombat: Boolean
        get() = (combatStartTime + combatDelay) > System.currentTimeMillis()


    fun enterCombat(profile: RPGProfile) {
        val time = System.currentTimeMillis()
        combatTimeMap[profile.id] = time
        combatStartTime = time
    }

    fun leaveCombat(profileId: UUID) {
        combatTimeMap.remove(profileId)
    }

    fun clear() {
        combatTimeMap.clear()
        combatStartTime = 0
    }
}