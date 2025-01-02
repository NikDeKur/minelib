@file:Suppress("unused")
package dev.nikdekur.minelib.rpg.stat

import dev.nikdekur.minelib.i18n.msg.DefaultMSG
import kotlinx.serialization.Serializable

/**
 * Uses high-scaled values speed-value.
 *
 * The high-scaled values are greater than minecraft 100 times
 *
 * The default speed-value is 20 (high-scaled) and 0.2 (minecraft)
 */
@Serializable
object RPGSpeedStat : RPGIntStat() {
    override val id: String = "speed"
    override val name = DefaultMSG.RPG.Stat.Speed.NAME
    override val nameBuff = DefaultMSG.RPG.Stat.Speed.BUFF_NAME
    override val defaultValue = 20
}


// ----------------------------
// HEALTH
// ----------------------------

@Serializable
object RPGHealthStat : RPGDoubleStat() {
    override val id: String = "health"
    override val name = DefaultMSG.RPG.Stat.Health.NAME
    override val nameBuff = DefaultMSG.RPG.Stat.Health.BUFF_NAME
    override val defaultValue = 20.0
}

@Serializable
object RPGMaxHealthStat : RPGDoubleStat() {
    override val id: String = "max_health"
    override val name = DefaultMSG.RPG.Stat.MaxHealth.NAME
    override val nameBuff = DefaultMSG.RPG.Stat.MaxHealth.BUFF_NAME
    override val defaultValue = 20.0
}

// ----------------------------
// HEALTH
// ----------------------------
@Serializable
object RPGProtectionStat : RPGDoubleStat() {
    override val id: String = "armor"
    override val name = DefaultMSG.RPG.Stat.Protection.NAME
    override val nameBuff = DefaultMSG.RPG.Stat.Protection.BUFF_NAME
}



// ----------------------------
// REGENERATION
// ----------------------------
@Serializable
object RPGRegenStat : RPGBigIntegerStat() {
    override val id: String = "regeneration"
    override val name = DefaultMSG.RPG.Stat.Regeneration.NAME
    override val nameBuff = DefaultMSG.RPG.Stat.Regeneration.BUFF_NAME
    override val defaultValue = 1.toBigInteger()
}

@Serializable
object RPGRegenMultiplierStat : RPGDoubleStat() {
    override val id: String = "regeneration_multiplier"
    override val name = DefaultMSG.RPG.Stat.RegenerationMultiplier.NAME
    override val nameBuff = DefaultMSG.RPG.Stat.RegenerationMultiplier.BUFF_NAME
    override val defaultValue = 1.0
}

@Serializable
object RPGRegenDelayStat : RPGIntStat() {
    override val id: String = "regeneration_delay"
    override val name = DefaultMSG.RPG.Stat.RegenerationDelay.NAME
    override val nameBuff = DefaultMSG.RPG.Stat.RegenerationDelay.BUFF_NAME
    override val defaultValue = 1000
}


// ----------------------------
// DAMAGE
// ----------------------------
@Serializable
object RPGDamageStat : RPGDoubleStat() {
    override val id: String = "damage"
    override val name = DefaultMSG.RPG.Stat.Damage.NAME
    override val nameBuff = DefaultMSG.RPG.Stat.Damage.BUFF_NAME
    override val defaultValue = 1.0
}

@Serializable
object RPGDamageMultiplierStat : RPGDoubleStat() {
    override val id: String = "damage_multiplier"
    override val name = DefaultMSG.RPG.Stat.DamageMultiplier.NAME
    override val nameBuff = DefaultMSG.RPG.Stat.DamageMultiplier.BUFF_NAME
    override val defaultValue = 1.0
}