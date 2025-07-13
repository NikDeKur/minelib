package dev.nikdekur.minelib.pentity

import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.service.PluginService
import org.bukkit.World
import java.util.*

open class RuntimePersonalEntityService(
    override val app: PluginApplication
) : PluginService(), PersonalEntityService {

    val managers: MutableMap<UUID, PersonalEntityManager> = hashMapOf()

    override suspend fun onDisable() {
        managers.values.forEach(PersonalEntityManager::removeAll)
        managers.clear()
    }

    override fun getManager(world: World): PersonalEntityManager {
        return managers.getOrPut(world.uid) { RuntimePersonalEntityManager(world) }
    }
}