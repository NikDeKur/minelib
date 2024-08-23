package dev.nikdekur.minelib.rpg

import dev.nikdekur.minelib.plugin.ServerPlugin
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.profile.RPGSimpleLivingEntityProfile
import dev.nikdekur.minelib.rpg.profile.RPGSimplePlayerProfile
import dev.nikdekur.minelib.rpg.strategy.DefaultDamageStrategy
import dev.nikdekur.minelib.utils.Utils.debug
import dev.nikdekur.ndkore.service.Service
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set
import kotlin.reflect.KClass

class RuntimeRPGProfilesService(
    override val app: ServerPlugin
) : RPGProfilesService {

    override val bindClass: KClass<out Service<*>>
        get() = RPGProfilesService::class

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


    override fun getProfile(obj: Any): RPGProfile? {
        debug("Getting profile for $obj")
        return when (obj) {
            is UUID -> rpgProfiles[obj]
            is LivingEntity -> rpgProfiles[obj.uniqueId] ?: createProfile(obj)
            else -> null
        }.also {
            debug("Got profile: $it")
        }
    }
}