@file:Suppress("NOTHING_TO_INLINE")

package dev.nikdekur.minelib.rpg


import dev.nikdekur.minelib.MineLib
import dev.nikdekur.minelib.rpg.condition.Condition
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.ndkore.ext.addById
import dev.nikdekur.ndkore.ext.getInstanceFieldOrNull
import dev.nikdekur.ndkore.reflect.ClassPathClassFinder
import java.util.*

class RuntimeRPGService(override val app: MineLib) : RPGService {

    override val bindClass
        get() = RPGService::class

    val stats = HashMap<String, RPGStat<*>>()
    val conditions = HashMap<String, Class<out Condition<*>>>()

    override fun onEnable() {
        ClassPathClassFinder.find(app.clazzLoader, STATS_PACKAGE) { it: Class<*> ->
            if (!RPGStat::class.java.isAssignableFrom(it)) return@find

            val instance = it.getInstanceFieldOrNull() as? RPGStat<*> ?: return@find
            registerStat(instance)
        }
    }

    override fun onDisable() {
        stats.clear()
    }


    override fun registerStat(type: RPGStat<*>) {
        stats.addById(type)
    }

    override fun getStat(id: String): RPGStat<*>? {
        return stats[id]
    }

    override fun registerCondition(clazz: Class<out Condition<*>>, id: String) {
        conditions[id] = clazz
    }

    override fun getCondition(id: String): Class<out Condition<*>>? {
        return conditions[id]
    }


    companion object {
        const val STATS_PACKAGE = "dev.nikdekur.minelib.rpg.stat"
    }
}