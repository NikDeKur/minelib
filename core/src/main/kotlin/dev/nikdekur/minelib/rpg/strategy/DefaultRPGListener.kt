package dev.nikdekur.minelib.rpg.strategy

import dev.nikdekur.minelib.koin.MineLibKoinComponent
import dev.nikdekur.minelib.rpg.DefaultRPGService
import dev.nikdekur.minelib.rpg.combat.DamageSource
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.koin.core.component.inject

object DefaultRPGListener : Listener, MineLibKoinComponent {

    val defaultRpgService: DefaultRPGService by inject()

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        val attacker = event.damager
        val victim = event.entity

        if (attacker == null || victim == null) return

        val attackerProfile = defaultRpgService.getProfile(attacker.uniqueId) ?: return
        val victimProfile = defaultRpgService.getProfile(victim.uniqueId) ?: return

        val damageSource = DamageSource.Physical(attacker.world, attackerProfile)
        victimProfile.damage(damageSource)
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val entity = event.entity
        val profile = defaultRpgService.getProfile(entity.uniqueId) ?: return
        when (event.cause) {
            EntityDamageEvent.DamageCause.CONTACT -> TODO()
            EntityDamageEvent.DamageCause.ENTITY_ATTACK -> TODO()
            EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK -> TODO()
            EntityDamageEvent.DamageCause.PROJECTILE -> TODO()
            EntityDamageEvent.DamageCause.SUFFOCATION -> TODO()
            EntityDamageEvent.DamageCause.FALL -> TODO()
            EntityDamageEvent.DamageCause.FIRE -> TODO()
            EntityDamageEvent.DamageCause.FIRE_TICK -> TODO()
            EntityDamageEvent.DamageCause.MELTING -> TODO()
            EntityDamageEvent.DamageCause.LAVA -> TODO()
            EntityDamageEvent.DamageCause.DROWNING -> TODO()
            EntityDamageEvent.DamageCause.BLOCK_EXPLOSION -> TODO()
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION -> TODO()
            EntityDamageEvent.DamageCause.VOID -> TODO()
            EntityDamageEvent.DamageCause.LIGHTNING -> TODO()
            EntityDamageEvent.DamageCause.SUICIDE -> TODO()
            EntityDamageEvent.DamageCause.STARVATION -> TODO()
            EntityDamageEvent.DamageCause.POISON -> TODO()
            EntityDamageEvent.DamageCause.MAGIC -> TODO()
            EntityDamageEvent.DamageCause.WITHER -> TODO()
            EntityDamageEvent.DamageCause.FALLING_BLOCK -> TODO()
            EntityDamageEvent.DamageCause.THORNS -> TODO()
            EntityDamageEvent.DamageCause.DRAGON_BREATH -> TODO()
            EntityDamageEvent.DamageCause.CUSTOM -> TODO()
            EntityDamageEvent.DamageCause.FLY_INTO_WALL -> TODO()
            EntityDamageEvent.DamageCause.HOT_FLOOR -> TODO()
            EntityDamageEvent.DamageCause.CRAMMING -> TODO()
        }
        val damageSource = DamageSource.Fall(entity.world, event.damage)
        profile.damage(damageSource)
    }
}