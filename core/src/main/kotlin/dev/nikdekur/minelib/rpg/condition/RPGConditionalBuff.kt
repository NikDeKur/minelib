package dev.nikdekur.minelib.rpg.condition

import dev.nikdekur.minelib.rpg.buff.RPGBuff
import dev.nikdekur.minelib.rpg.stat.RPGStat

open class RPGConditionalBuff<T : Comparable<T>>(
    type: RPGStat<T>,
    value: T,
    val types: Set<ConditionType>,
    val condition: () -> Boolean
) : RPGBuff<T>(type, value) {

    fun check(): Boolean {
        return condition()
    }
}