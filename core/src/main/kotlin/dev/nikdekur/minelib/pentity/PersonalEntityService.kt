package dev.nikdekur.minelib.pentity

import org.bukkit.World


interface PersonalEntityService {
    fun getManager(world: World): PersonalEntityManager
}