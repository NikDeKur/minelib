package dev.nikdekur.minelib.rpg.profile

import dev.nikdekur.minelib.ext.call
import dev.nikdekur.minelib.rpg.buff.AttachableBuffsListImpl
import dev.nikdekur.minelib.rpg.buff.RPGBuff
import dev.nikdekur.minelib.rpg.combat.DamageSource
import dev.nikdekur.minelib.rpg.event.RPGDamageEvent
import dev.nikdekur.minelib.rpg.event.RPGKillEvent
import dev.nikdekur.minelib.rpg.stat.RPGHealthStat
import dev.nikdekur.minelib.rpg.stat.RPGMaxHealthStat
import dev.nikdekur.minelib.rpg.stat.RPGProfileStats
import dev.nikdekur.minelib.rpg.strategy.DefaultDamageStrategy
import dev.nikdekur.minelib.rpg.update.FixedRateUpdater
import java.util.*

abstract class RPGSimpleProfile : RPGProfile {

    final override val stats by lazy {
        RPGProfileStats(this)
    }

    override val strategy = DefaultDamageStrategy


    override val buffs = object : AttachableBuffsListImpl() {
        override fun <T : Comparable<T>> afterAddBuff(buff: RPGBuff<T>) {
            stats.add(buff.stat, buff.value)
        }

        override fun <T : Comparable<T>> afterRemoveBuff(buff: RPGBuff<T>) {
            stats.take(buff.stat, buff.value)
        }
    }


    override var applied: Boolean = false

    override fun apply() {
        applied = true
    }

    override fun unApply() {
        applied = false
    }

    


    override fun heal(value: Double) {
        val max = stats[RPGMaxHealthStat]
        val current = stats[RPGHealthStat]
        stats[RPGHealthStat] = if ((current + value) > max) {
            max
        } else {
            value
        }
    }

    override fun healProcent(procent: Double) {
        val maxHealth = stats[RPGMaxHealthStat]
        val toHeal = maxHealth / 100 * procent
        heal(toHeal)
    }

    override fun damage(source: DamageSource): Double  {
        var finalDamage = damage
        if (finalDamage > health) {
            finalDamage = health
        }

        val event = RPGDamageEvent(this, source, finalDamage)
        event.call()
        if (event.isCancelled) return 0.0
        finalDamage = event.damage

        if (health - finalDamage <= 0.0) {
            kill(source)
        } else {
            val newHealth = health - finalDamage
            stats[RPGHealthStat] = newHealth
        }

        return damage
    }

    override fun kill(source: DamageSource) {
        stats[RPGHealthStat] = 0.0
        RPGKillEvent(this, source).call()
    }


    override fun clear() {
        stats.clear()
        buffs.clear()
    }


    open val updaters = HashMap<UUID, FixedRateUpdater>()

    override fun addUpdater(updater: FixedRateUpdater, start: Boolean) {
        updaters[updater.id] = updater
        if (start) updater.start()
    }


    override fun getUpdate(id: UUID): FixedRateUpdater? {
        return updaters[id]
    }

    override fun removeUpdater(id: UUID): FixedRateUpdater? {
        val regen = updaters.remove(id)
        regen?.cancel()
        return regen
    }
}