@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg


import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.service.PluginService
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.ext.addById
import dev.nikdekur.ndkore.ext.getInstanceFieldOrNull
import dev.nikdekur.ndkore.extra.Tools
import java.util.*
import kotlin.reflect.KClass

class DefaultRPGService(override val app: MineLib) : RPGService, PluginService {

    override val bindClass: KClass<*>
        get() = RPGService::class

    val stats = HashMap<String, RPGStat<*>>()

    override fun onLoad() {
        val classes = Tools.findClasses(app.clazzLoader, STATS_PACKAGE)
        classes.forEach {
            if (!RPGStat::class.java.isAssignableFrom(it)) return@forEach

            val instance = it.getInstanceFieldOrNull() as? RPGStat<*> ?: return@forEach
            registerStat(instance)
        }
    }

    override fun onUnload() {
        stats.clear()
    }


    override fun registerStat(type: RPGStat<*>) {
        stats.addById(type)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Comparable<T>> getStat(id: String): RPGStat<T>? {
        return stats[id] as? RPGStat<T>
    }






    val rpgProfiles = HashMap<UUID, RPGProfile>()

    /**
     * Add profile to registry by specified id.
     *
     * The reason why it doesn't use the profile id is because we can provide alias-ids for the profile.
     * The simple example of it, it's adding player's current RPGProfile in registry by player's UUID,
     * to have a way to get it by player's UUID.
     *
     * @param id The id to store the profile by.
     * @param profile The profile to store.
     */
    override fun addProfile(id: UUID, profile: RPGProfile) {
        rpgProfiles[id] = profile
    }
    override fun removeProfile(profileId: UUID) {
        rpgProfiles.remove(profileId)
    }
    override fun getProfile(profileId: UUID): RPGProfile? {
        return rpgProfiles[profileId]
    }


    companion object {
        const val STATS_PACKAGE = "dev.nikdekur.minelib.rpg.stat"
    }
}