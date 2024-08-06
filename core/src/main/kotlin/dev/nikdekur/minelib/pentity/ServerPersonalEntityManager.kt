package dev.nikdekur.minelib.pentity

import dev.nikdekur.minelib.MineLibModule
import org.bukkit.World

interface ServerPersonalEntityManager : MineLibModule {

    fun getManager(world: World): PersonalEntityManager
}