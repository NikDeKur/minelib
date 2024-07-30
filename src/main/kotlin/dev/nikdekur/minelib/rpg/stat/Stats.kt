@file:Suppress("unused")
package dev.nikdekur.minelib.rpg.stat

import dev.nikdekur.minelib.i18n.DefaultMSG

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
}


// ----------------------------
// HEALTH
// ----------------------------

object RPGHealthStat : RPGStat.Double() {
    override val id: String = "health"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_HEALTH
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_HEALTH
}

object RPGMaxHealthStat : RPGStat.Double() {
    override val id: String = "max_health"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_MAX_HEALTH
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_MAX_HEALTH
}


// ----------------------------
// REGENERATION
// ----------------------------
object RPGRegenStat : RPGStat.BigInteger() {
    override val id: String = "regeneration"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_REGENERATION
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_REGENERATION
}

object RPGRegenMultiplierStat : RPGStat.Double() {
    override val id: String = "regeneration_multiplier"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_REGENERATION_MULTIPLIER
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_REGENERATION_MULTIPLIER
}


// ----------------------------
// DAMAGE
// ----------------------------
object RPGDamageStat : RPGStat.Double() {
    override val id: String = "damage"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_DAMAGE
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_DAMAGE
}

object RPGDamageMultiplierStat : RPGStat.Double() {
    override val id: String = "damage_multiplier"
    override val nameMSG = DefaultMSG.RPG_STAT_NAME_DAMAGE_MULTIPLIER
    override val nameBuffMSG = DefaultMSG.RPG_STAT_NAME_BUFF_DAMAGE_MULTIPLIER
}