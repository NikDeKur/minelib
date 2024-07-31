package dev.nikdekur.minelib.rpg.combat

import dev.nikdekur.minelib.rpg.profile.RPGProfile
import org.bukkit.World

interface DamageSource {

    val type: String
    val world: World

    object Type {
        const val FIRE = "FIRE"
        const val DROWN = "DROWN"
        const val FALL = "FALL"
        const val CUSTOM = "CUSTOM"
    }

    interface RPG : DamageSource {
        val attacker: RPGProfile
    }

    class Fire(
        override val world: World,
        val count: Int
    ) : DamageSource {
        override val type = Type.FIRE
    }

    class Drown(
        override val world: World,
        val depth: Int
    ) : DamageSource {
        override val type = Type.DROWN
    }

    class Fall(
        override val world: World,
        val height: Int
    ) : DamageSource {
        override val type = Type.FALL
    }

    class Unknown(
        override val world: World
    ) : DamageSource {
        override val type = Type.CUSTOM
    }


    companion object {
        fun fire(world: World, count: Int) = Fire(world, count)
        fun drown(world: World, depth: Int) = Drown(world, depth)
        fun fall(world: World, height: Int) = Fall(world, height)
        fun unknown(world: World) = Unknown(world)
    }
}