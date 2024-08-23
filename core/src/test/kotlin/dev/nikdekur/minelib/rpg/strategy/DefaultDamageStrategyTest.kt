@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg.strategy

import dev.nikdekur.minelib.rpg.combat.DamageSource
import dev.nikdekur.minelib.rpg.profile.RPGSimpleProfile
import dev.nikdekur.minelib.rpg.stat.RPGDamageStat
import dev.nikdekur.minelib.rpg.stat.RPGMaxHealthStat
import dev.nikdekur.minelib.rpg.stat.RPGProtectionStat
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class DefaultDamageStrategyTest {

    inline fun newProfile() = object : RPGSimpleProfile() {
        override val strategy = DefaultDamageStrategy
        override val id = UUID.randomUUID()
    }

    @Test
    fun `test default damage`() {
        val attacker = newProfile()
        val victim = newProfile()

        val source = DamageSource.Physical(attacker)
        victim.damage(source)

        val expectedHealth = RPGMaxHealthStat.defaultValue - RPGDamageStat.defaultValue

        assertEquals(expectedHealth, victim.health)
    }

    @Test
    fun `test custom damage`() {
        val attacker = newProfile()
        val victim = newProfile()

        val damage = 10.0
        attacker.stats[RPGDamageStat] = damage
        val expectedHealth = RPGMaxHealthStat.defaultValue - damage

        val source = DamageSource.Physical(attacker)
        victim.damage(source)

        assertEquals(expectedHealth, victim.health)
    }

    @Test
    fun `test custom damage with protection`() {
        val attacker = newProfile()
        val victim = newProfile()

        val damage = 15.0
        attacker.stats[RPGDamageStat] = damage

        val protection = 150.0
        victim.stats[RPGProtectionStat] = protection

        val source = DamageSource.Physical(attacker)
        victim.damage(source)

        println(victim.health)

    }
}
