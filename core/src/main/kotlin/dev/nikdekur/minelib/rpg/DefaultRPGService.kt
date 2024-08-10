@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg


import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.MineLibModule
import dev.nikdekur.minelib.ext.getLangMsg
import dev.nikdekur.minelib.rpg.buff.RPGBuff
import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.ext.addById
import dev.nikdekur.ndkore.ext.getInstanceFieldOrNull
import dev.nikdekur.ndkore.extra.Tools
import org.bukkit.command.CommandSender
import java.util.*

class DefaultRPGService(override val app: MineLib) : RPGService, MineLibModule {
    val stats = HashMap<String, RPGStat<*>>()

    override fun onLoad() {
        val classes = Tools.findClasses(app.loader, STATS_PACKAGE)
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

    @Suppress("UNCHECKED_CAST")
    private inline fun <T : Comparable<T>> unsafePlus(stat: RPGStat<T>, a: Any, b: Any): Any {
        return stat.plus(a as T, b as T)
    }

    fun formatToLore(buffs: Iterable<RPGBuff<*>>, player: CommandSender): ArrayList<String> {
        val res = ArrayList<String>()
        val stats = HashMap<RPGStat<*>, Any>()
        buffs.forEach {
            stats.compute(it.stat) { _, v ->
                if (v == null) it.value
                else unsafePlus(it.stat, it.value, v)
            }
        }

        stats.forEach { (stat, value) ->
            val message = player.getLangMsg(
                stat.nameBuffMSG,
                "stat" to stat.getPlaceholder(player),
                "buff" to listOf(
                    "value", value,
                    "name", player.getLangMsg(stat.nameMSG).text
                )
            )

            res.add(message.text)
        }
        return res
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