package dev.nikdekur.minelib.rpg.buff

import dev.nikdekur.minelib.rpg.profile.RPGProfile
import dev.nikdekur.minelib.rpg.stat.RPGStat

open class RPGConditionalBuff<T : Comparable<T>>(
    type: RPGStat<T>,
    value: T,
    val condition: (RPGProfile) -> Boolean
) : RPGBuff<T>(type, value) {

    fun check(profile: RPGProfile): Boolean {
        return condition(profile)
    }
}