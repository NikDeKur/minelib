package dev.nikdekur.minelib.rpg.strategy

import dev.nikdekur.minelib.rpg.combat.DamageSource
import dev.nikdekur.minelib.rpg.profile.RPGLivingEntityProfile
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.profile.RPGSimpleProfile
import org.bukkit.enchantments.Enchantment

object DefaultDamageStrategy : DamageStrategy {
    override fun calculateDamage(profile: RPGSimpleProfile, source: DamageSource): Double {
        return when (source) {
            is DamageSource.RPG -> fightingDamage(source.attacker, profile, source)

            // TODO: Dont forget about enchantments and potions
            is DamageSource.Fire -> source.count * 1.5
            is DamageSource.Drown -> source.depth * 2.0
            is DamageSource.Fall -> source.height
            else -> 0.0
        }
    }

    fun fightingDamage(attacker: RPGProfile, victim: RPGProfile, source: DamageSource): Double {
        val protection = calcEnvProtectionPoints(victim)
        val damage = attacker.damage / protection
        return damage
    }


    fun calcEnvProtectionPoints(profile: RPGProfile): Double {
        val equipment = if (profile is RPGLivingEntityProfile) {
            val entity = profile.entity
            entity.equipment
        } else return 0.0

        var points = 0.0
        equipment.armorContents.forEach {
            if (it == null) return@forEach

            val type = it.type
            val name = type.name
            val partPoints = when {
                name.contains("HELMET") -> 5.0
                name.contains("CHESTPLATE") -> 8.0
                name.contains("LEGGINGS") -> 7.0
                name.contains("BOOTS") -> 4.0
                else -> 0.0
            }

            val materialPoints = when {
                name.contains("NETHERITE") -> 5.0
                name.contains("DIAMOND") -> 4.0
                name.contains("IRON") -> 3.0
                name.contains("CHAINMAIL") -> 2.0
                name.contains("GOLD") -> 2.0
                name.contains("LEATHER") -> 1.0
                else -> 0.0
            }

            val base = partPoints * materialPoints

            val protectionLevel = it.enchantments.getOrDefault(Enchantment.PROTECTION_ENVIRONMENTAL, 0)
            val protectionPoints = base / 4 * protectionLevel
            points += base + protectionPoints
        }

        // Diamond Helmet with protection 4 = 20 + 20 = 40
        // Diamond Chestplate with protection 4 = 32 + 32 = 64
        // Diamond Leggings with protection 4 = 28 + 28 = 56
        // Diamond Boots with protection 4 = 16 + 16 = 32
        // Total = 192

        // Diamond Helmet = 20
        // Diamond Chestplate = 32
        // Diamond Leggings = 28
        // Diamond Boots = 16
        // Total = 96

        // Iron Helmet with protection 4 = 15 + 15 = 30
        // Iron Chestplate with protection 4 = 24 + 24 = 48
        // Iron Leggings with protection 4 = 21 + 21 = 42
        // Iron Boots with protection 4 = 12 + 12 = 24
        // Total = 144

        // Iron Helmet = 15
        // Iron Chestplate = 24
        // Iron Leggings = 21
        // Iron Boots = 12
        // Total = 72




        return points / 10
    }
}