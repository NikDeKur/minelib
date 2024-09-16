package dev.nikdekur.minelib.rpg.combat

import dev.nikdekur.minelib.rpg.profile.RPGProfile

interface DamageSource {

    abstract class RPG : DamageSource {
        abstract val attacker: RPGProfile
        override fun toString() = "DamageSource.RPG(attacker=$attacker)"
    }

    class Physical(
        override val attacker: RPGProfile
    ) : RPG() {
        override fun toString() = "DamageSource.Physical(attacker=$attacker)"
    }

    /**
     * Fire damage source when player stands in fire
     */
    class Fire(
        val count: Int
    ) : DamageSource {
        override fun toString() = "DamageSource.Fire(count=$count)"
    }

    /**
     * Fire damage source when player burns, not [Fire]
     */
    class FireTick(
        val count: Int
    ) : DamageSource {
        override fun toString() = "DamageSource.FireTick(count=$count)"
    }

    class Drown(
        val depth: Int
    ) : DamageSource {
        override fun toString() = "DamageSource.Drown(depth=$depth)"
    }

    class Fall(
        val height: Double
    ) : DamageSource {
        override fun toString() = "DamageSource.Fall(height=$height)"
    }

    class Unknown : DamageSource {
        override fun toString() = "DamageSource.Unknown"
    }
}