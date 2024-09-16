package dev.nikdekur.minelib.rpg.profile

import dev.nikdekur.minelib.rpg.strategy.DamageStrategy
import org.bukkit.entity.Player

open class RPGSimplePlayerProfile(
    override val player: Player,
    strategy: DamageStrategy,
) : RPGSimpleLivingEntityProfile(player, strategy), RPGPlayerProfile {
    override val entity: Player
        get() = player


    override fun toString(): String {
        return "RPGSimplePlayerProfile(player=$player, strategy=$strategy, stats=$stats)"
    }
}