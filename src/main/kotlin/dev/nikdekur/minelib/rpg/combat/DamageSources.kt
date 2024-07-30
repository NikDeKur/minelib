package dev.nikdekur.minelib.rpg.combat

import org.bukkit.World
import dev.nikdekur.minelib.rpg.profile.RPGProfile

interface RPGDamageSource : DamageSource {
    val rpg: RPGProfile
}







class FireDamageSource(
    override val world: World,
    val count: Int
) : DamageSource {
    override val type = DamageSource.Type.FIRE
}

class DrownDamageSource(
    override val world: World,
    val depth: Int
) : DamageSource {
    override val type = DamageSource.Type.DROWN
}

class FallDamageSource(
    override val world: World,
    val height: Int
) : DamageSource {
    override val type = DamageSource.Type.FALL
}

class CustomDamageSource(
    override val world: World
) : DamageSource {
    override val type = DamageSource.Type.CUSTOM
}