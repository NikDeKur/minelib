package dev.nikdekur.minelib.rpg.combat

import dev.nikdekur.minelib.rpg.profile.RPGProfile
import org.bukkit.World

interface DamageSource {

    val world: World

    interface RPG : DamageSource {
        val attacker: RPGProfile
    }

    class Physical(
        override val world: World,
        override val attacker: RPGProfile
    ) : RPG

    /**
     * Fire damage source when player stands in fire
     */
    class Fire(
        override val world: World,
        val count: Int
    ) : DamageSource

    /**
     * Fire damage source when player burns, not [Fire]
     */
    class FireTick(
        override val world: World,
        val count: Int
    ) : DamageSource

    class Drown(
        override val world: World,
        val depth: Int
    ) : DamageSource

    class Fall(
        override val world: World,
        val height: Double
    ) : DamageSource

    class Unknown(
        override val world: World
    ) : DamageSource

}