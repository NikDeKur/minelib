package dev.nikdekur.minelib.pentity

import org.bukkit.World

interface ServerPersonalEntityManager {

    fun getManager(world: World): PersonalEntityManager
}