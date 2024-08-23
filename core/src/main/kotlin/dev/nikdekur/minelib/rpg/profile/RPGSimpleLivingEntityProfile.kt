package dev.nikdekur.minelib.rpg.profile

import dev.nikdekur.minelib.ext.walkingSpeed
import dev.nikdekur.minelib.rpg.stat.RPGHealthStat
import dev.nikdekur.minelib.rpg.stat.RPGMaxHealthStat
import dev.nikdekur.minelib.rpg.stat.RPGSpeedStat
import dev.nikdekur.minelib.rpg.strategy.DamageStrategy
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity

open class RPGSimpleLivingEntityProfile(
    override val entity: LivingEntity,
    override val strategy: DamageStrategy
) : RPGSimpleProfile(), RPGLivingEntityProfile {

    override val id by entity::uniqueId

    init { stats.apply {
        afterStatChange(RPGMaxHealthStat) {
            updateMaxHealth(maxHealth)

            // If max health increases, we increase the health by the same amount
            if (isIncrease) {
                val added = newValue - oldValue
                heal(added)
            } else {
                val taken = oldValue - newValue
                if (health > maxHealth)
                    stats.take(RPGHealthStat, taken)
            }
        }

        afterStatChange(RPGHealthStat) {
            updateHealth(health)
        }

        afterStatChange(RPGSpeedStat) {
            updateSpeed(stats[RPGSpeedStat])
        }
    } }

    private fun updateSpeed(value: Int) {
        entity.walkingSpeed = value / 100.0f
    }

    private fun updateHealth(value: Double) {
        entity.health = value
    }

    private fun updateMaxHealth(value: Double) {
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = value
    }



    override fun apply() {
        super.apply()
        updateMaxHealth(maxHealth)
        updateHealth(health)
        updateSpeed(stats[RPGSpeedStat])
    }

    override fun unApply() {
        super.unApply()
        updateMaxHealth(20.0)
        updateHealth(20.0)
        updateSpeed(20)
    }
}