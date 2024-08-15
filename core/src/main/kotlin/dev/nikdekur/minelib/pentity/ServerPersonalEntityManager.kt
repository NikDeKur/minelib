package dev.nikdekur.minelib.pentity

import org.bukkit.World

fun interface ServerPersonalEntityManager {

    fun getManager(world: World): PersonalEntityManager
}