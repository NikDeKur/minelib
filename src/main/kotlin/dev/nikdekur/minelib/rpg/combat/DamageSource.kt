package dev.nikdekur.minelib.rpg.combat

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



    companion object {
        fun fire(world: World, count: Int) = FireDamageSource(world, count)
        fun drown(world: World, depth: Int) = DrownDamageSource(world, depth)
        fun fall(world: World, height: Int) = FallDamageSource(world, height)
        fun custom(world: World) = CustomDamageSource(world)
    }
}