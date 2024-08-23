package dev.nikdekur.minelib.pentity

import dev.nikdekur.minelib.service.PluginService
import org.bukkit.World

interface ServerPersonalEntityManager : PluginService {

    fun getManager(world: World): PersonalEntityManager
}