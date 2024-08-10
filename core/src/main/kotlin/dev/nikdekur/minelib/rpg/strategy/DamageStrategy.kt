package dev.nikdekur.minelib.rpg.strategy

import dev.nikdekur.minelib.rpg.combat.DamageSource
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.profile.RPGSimpleProfile

/**
 * Represents a strategy for dealing damage.
 *
 * May be set for [RPGProfile] to change the main way damage is calculated.
 */
fun interface DamageStrategy {

    /**
     * Calculates the damage dealt to the profile due to the source.
     *
     * @param profile the profile to deal damage to
     * @param source the source of the damage
     * @return the damage dealt
     */
    fun calculateDamage(profile: RPGSimpleProfile, source: DamageSource): Double
}