package dev.nikdekur.minelib.rpg

import dev.nikdekur.minelib.app.PluginApplication
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.profile.RPGSimpleLivingEntityProfile
import dev.nikdekur.minelib.rpg.profile.RPGSimplePlayerProfile
import dev.nikdekur.minelib.rpg.strategy.DefaultDamageStrategy
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.minelib.utils.debug
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class RuntimeRPGProfilesService(
    override val app: PluginApplication
) : PluginService(), RPGProfilesService {

    val rpgProfiles = ConcurrentHashMap<UUID, RPGProfile>()

    fun createProfile(entity: LivingEntity): RPGProfile {
        debug("Creating profile for $entity")
        val strategy = DefaultDamageStrategy
        return when (entity) {
            is Player -> RPGSimplePlayerProfile(entity, strategy)
            else -> RPGSimpleLivingEntityProfile(entity, strategy)
        }.also {
            debug("Created profile: $it")
            rpgProfiles[entity.uniqueId] = it
        }
    }


    override fun getExistingProfile(obj: Any): RPGProfile? {
        return when (obj) {
            is UUID -> rpgProfiles[obj]
            is LivingEntity -> rpgProfiles[obj.uniqueId]
            else -> null
        }
    }

    override fun getProfile(obj: Any): RPGProfile? {
        return when (obj) {
            is UUID -> rpgProfiles[obj]
            is LivingEntity -> rpgProfiles[obj.uniqueId] ?: createProfile(obj)
            else -> null
        }
    }
}