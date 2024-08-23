package dev.nikdekur.minelib.rpg.combat

import dev.nikdekur.minelib.rpg.profile.RPGProfile

interface DamageSource {

    interface RPG : DamageSource {
        val attacker: RPGProfile
    }

    class Physical(
        override val attacker: RPGProfile
    ) : RPG

    /**
     * Fire damage source when player stands in fire
     */
    class Fire(
        val count: Int
    ) : DamageSource

    /**
     * Fire damage source when player burns, not [Fire]
     */
    class FireTick(
        val count: Int
    ) : DamageSource

    class Drown(
        val depth: Int
    ) : DamageSource

    class Fall(
        val height: Double
    ) : DamageSource

    class Unknown : DamageSource

}