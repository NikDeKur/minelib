package dev.nikdekur.minelib.rpg

import dev.nikdekur.minelib.rpg.condition.Condition
import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.minelib.service.PluginService

interface RPGService : PluginService {

    fun registerStat(stat: RPGStat<*>)

    fun getStat(id: String): RPGStat<*>?

    fun registerCondition(clazz: Class<out Condition<*>>, id: String)
    fun getCondition(id: String): Class<out Condition<*>>?
}


