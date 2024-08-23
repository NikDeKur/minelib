package dev.nikdekur.minelib.rpg

import dev.nikdekur.minelib.rpg.stat.RPGStat
import dev.nikdekur.minelib.service.PluginService

interface RPGService : PluginService {

    fun registerStat(stat: RPGStat<*>)

    fun <T : Comparable<T>> getStat(id: String): RPGStat<T>?
}


