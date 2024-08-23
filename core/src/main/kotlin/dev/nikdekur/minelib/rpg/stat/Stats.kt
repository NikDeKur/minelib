@file:Suppress("unused")
package dev.nikdekur.minelib.rpg.stat

import dev.nikdekur.minelib.i18n.msg.DefaultMSG

/**
 * Uses high-scaled values speed-value.
 *
 * The high-scaled values are greater than minecraft 100 times
 *
 * The default speed-value is 20 (high-scaled) and 0.2 (minecraft)
 */
object RPGSpeedStat : RPGStat.Int() {
    override val id: String = "speed"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_SPEED_BONUS
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_SPEED_BONUS
    override val defaultValue = 20
}


// ----------------------------
// HEALTH
// ----------------------------

object RPGHealthStat : RPGStat.Double() {
    override val id: String = "health"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_HEALTH
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_HEALTH
    override val defaultValue = 20.0
}

object RPGMaxHealthStat : RPGStat.Double() {
    override val id: String = "max_health"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_MAX_HEALTH
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_MAX_HEALTH
    override val defaultValue = 20.0
}

// ----------------------------
// HEALTH
// ----------------------------
object RPGProtectionStat : RPGStat.Double() {
    override val id: String = "armor"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_ARMOR
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_ARMOR
}



// ----------------------------
// REGENERATION
// ----------------------------
object RPGRegenStat : RPGStat.BigInteger() {
    override val id: String = "regeneration"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_REGENERATION
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_REGENERATION
    override val defaultValue = 1.toBigInteger()
}

object RPGRegenMultiplierStat : RPGStat.Double() {
    override val id: String = "regeneration_multiplier"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_REGENERATION_MULTIPLIER
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_REGENERATION_MULTIPLIER
    override val defaultValue = 1.0
}

object RPGRegenDelayStat : RPGStat.Int() {
    override val id: String = "regeneration_delay"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_REGENERATION_DELAY
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_REGENERATION_DELAY
    override val defaultValue = 1000
}


// ----------------------------
// DAMAGE
// ----------------------------
object RPGDamageStat : RPGStat.Double() {
    override val id: String = "damage"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_DAMAGE
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_DAMAGE
    override val defaultValue = 1.0
}

object RPGDamageMultiplierStat : RPGStat.Double() {
    override val id: String = "damage_multiplier"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_DAMAGE_MULTIPLIER
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_DAMAGE_MULTIPLIER
    override val defaultValue = 1.0
}