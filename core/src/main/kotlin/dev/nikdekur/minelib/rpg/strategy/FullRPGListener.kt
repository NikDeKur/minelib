package dev.nikdekur.minelib.rpg.strategy

import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.rpg.RPGProfilesService
import dev.nikdekur.minelib.rpg.combat.DamageSource
import dev.nikdekur.minelib.service.PluginListener
import dev.nikdekur.ndkore.service.inject
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent

class FullRPGListener(override val app: ServerPlugin) : PluginListener {

    val rpgService: RPGProfilesService by inject()

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        val attacker = event.damager
        val victim = event.entity

        if (attacker == null || victim == null) return

        val attackerProfile = rpgService.getProfile(attacker) ?: return
        val victimProfile = rpgService.getProfile(victim) ?: return

        val damageSource = DamageSource.Physical(attackerProfile)
        victimProfile.damage(damageSource)

        event.damage = 0.0
    }

//    @EventHandler
//    fun onDamage(event: EntityDamageEvent) {
//        val entity = event.entity
//        val profile = rpgService.getProfile(entity) ?: return
//        when (event.cause) {
//            EntityDamageEvent.DamageCause.CONTACT -> TODO()
//            EntityDamageEvent.DamageCause.ENTITY_ATTACK -> TODO()
//            EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK -> TODO()
//            EntityDamageEvent.DamageCause.PROJECTILE -> TODO()
//            EntityDamageEvent.DamageCause.SUFFOCATION -> TODO()
//            EntityDamageEvent.DamageCause.FALL -> TODO()
//            EntityDamageEvent.DamageCause.FIRE -> TODO()
//            EntityDamageEvent.DamageCause.FIRE_TICK -> TODO()
//            EntityDamageEvent.DamageCause.MELTING -> TODO()
//            EntityDamageEvent.DamageCause.LAVA -> TODO()
//            EntityDamageEvent.DamageCause.DROWNING -> TODO()
//            EntityDamageEvent.DamageCause.BLOCK_EXPLOSION -> TODO()
//            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION -> TODO()
//            EntityDamageEvent.DamageCause.VOID -> TODO()
//            EntityDamageEvent.DamageCause.LIGHTNING -> TODO()
//            EntityDamageEvent.DamageCause.SUICIDE -> TODO()
//            EntityDamageEvent.DamageCause.STARVATION -> TODO()
//            EntityDamageEvent.DamageCause.POISON -> TODO()
//            EntityDamageEvent.DamageCause.MAGIC -> TODO()
//            EntityDamageEvent.DamageCause.WITHER -> TODO()
//            EntityDamageEvent.DamageCause.FALLING_BLOCK -> TODO()
//            EntityDamageEvent.DamageCause.THORNS -> TODO()
//            EntityDamageEvent.DamageCause.DRAGON_BREATH -> TODO()
//            EntityDamageEvent.DamageCause.CUSTOM -> TODO()
//            EntityDamageEvent.DamageCause.FLY_INTO_WALL -> TODO()
//            EntityDamageEvent.DamageCause.HOT_FLOOR -> TODO()
//            EntityDamageEvent.DamageCause.CRAMMING -> TODO()
//        }
//        val damageSource = DamageSource.Fall(entity.world, event.damage)
//        profile.damage(damageSource)
//    }
}