package dev.nikdekur.minelib.rpg.profile

import dev.nikdekur.minelib.rpg.DefaultRPGService
import dev.nikdekur.minelib.rpg.buff.AttachableBuffsList
import dev.nikdekur.minelib.rpg.combat.DamageSource
import dev.nikdekur.minelib.rpg.stat.*
import dev.nikdekur.minelib.rpg.stat.RPGProfileStats
import dev.nikdekur.minelib.rpg.strategy.DamageStrategy
import dev.nikdekur.minelib.rpg.update.FixedRateUpdater
import dev.nikdekur.ndkore.`interface`.Snowflake
import java.util.*

interface RPGProfile : Snowflake<UUID> {

    val strategy: DamageStrategy

    val buffs: AttachableBuffsList

    // val defaultStats: Map<RPGStat<*>, Any>
    val stats: RPGProfileStats

    // Quick vars for stats
    var health: Double
        get() = stats[RPGHealthStat]
        set(value) { stats[RPGHealthStat] = value }
    var maxHealth: Double
        get() = stats[RPGMaxHealthStat]
        set(value) { stats[RPGMaxHealthStat] = value }
    var speed: Int
        get() = stats[RPGSpeedStat]
        set(value) { stats[RPGSpeedStat] = value }
    var damage: Double
        get() = stats[RPGDamageStat]
        set(value) { stats[RPGDamageStat] = value }



    val applied: Boolean

    /**
     * Apply the profile to the object
     *
     * Should make all stats work and apply all buffs to the object
     */
    fun apply()

    /**
     * Unapply the profile from the object
     *
     * Should stop (not remove) all stats and remove all buffs from the object
     */
    fun unApply()

    /**
     * Heal the player for the given amount of health
     *
     * Increase the health value, but doesn't change or exceed the max health
     */
    fun heal(value: Double)

    /**
     * Heal the player for the given procent (0-100) of max health
     *
     * Would not heal more than max's health
     *
     * @param procent The procent of max health to heal
     * @see heal
     */
    fun healProcent(procent: Double)

    /**
     * Damage the profile for the given amount of damage
     *
     * Method can decrease or cancel damage depending on the player's stats and damage caused.
     *
     * @param damage The damage to apply
     * @param source The source of the damage
     * @return The actual damage that was applied to the player
     */
    fun damage(source: DamageSource): Double


    /**
     * Kill the profile
     *
     * Calls [RPGKillEvent] and remove profile from the [DefaultRPGService]
     *
     * @param source The source of the damage
     */
    fun kill(source: DamageSource)


    /**
     * Clear all profile data
     *
     * Remove all buffs and reset all stats
     */
    fun clear()


    fun addUpdater(updater: FixedRateUpdater, start: Boolean)
    fun getUpdate(id: UUID): FixedRateUpdater?
    fun removeUpdater(id: UUID): FixedRateUpdater?
}