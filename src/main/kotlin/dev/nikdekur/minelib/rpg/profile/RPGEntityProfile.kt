//package dev.nikdekur.minelib.rpg.profile
//
//import dev.nikdekur.minelib.ext.walkingSpeed
//import dev.nikdekur.minelib.nms.packet.PacketBuilder
//import dev.nikdekur.minelib.rpg.combat.DamageSource
//import dev.nikdekur.minelib.rpg.stat.RPGHealthStat
//import dev.nikdekur.minelib.rpg.stat.RPGSpeedStat
//import org.bukkit.attribute.Attribute
//import org.bukkit.entity.LivingEntity
//
//abstract class RPGEntityProfile(val entity: LivingEntity) : RPGSimpleProfile() {
//
//    init {
//        stats.afterStatChange(RPGSpeedStat) {
//            updateSpeed()
//        }
//
//
//    }
//
//    /**
//     * Function that called in [apply] and should set the default values for some stats.
//     *
//     * Default implementation sets the health to max health.
//     */
//    open fun setApplyStats() {
//        stats[RPGHealthStat] = maxHealth
//    }
//
//
//    override fun apply() {
//        super.apply()
//        updateSpeed()
//        updateMaxHealth()
//        setApplyStats()
//    }
//
//
//
//
//
//
//
//
//
//    open fun updateSpeed() {
//        entity.walkingSpeed = stats[RPGSpeedStat] / 100.0f
//    }
//
//    open fun updateMaxHealth() {
//        val entity = entity
//        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = maxHealth
//    }
//
//
//    override fun damageRaw(damage: Double, source: DamageSource): Boolean {
//        return super.damageRaw(damage, source).also {
//            val entity = entity
//            PacketBuilder.Entity.Animation.takeDamage(entity.entityId).broadcast(entity.world)
//        }
//    }
//}