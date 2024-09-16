package dev.nikdekur.minelib.rpg.stat

class StatsMap {

    val stats = HashMap<RPGStat<*>, Any>()

    fun getRaw(stat: RPGStat<*>): Any? {
        return stats[stat]
    }

    fun <T : Comparable<T>> getOrNull(stat: RPGStat<T>): T? {
        val raw = getRaw(stat) ?: return null
        @Suppress("UNCHECKED_CAST") // Type erasure
        return stat.plus(raw as T, stat.staticValue)
    }

    operator fun <T : Comparable<T>> get(stat: RPGStat<T>): T {
        return getOrNull(stat) ?: stat.defaultValue
    }

    operator fun <T : Comparable<T>> set(stat: RPGStat<T>, value: T) {
        stats[stat] = value
    }

    fun clear() {
        stats.clear()
    }

    override fun toString(): String {
        return stats.toString()
    }
}