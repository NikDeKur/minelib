package dev.nikdekur.minelib.rpg.condition

import java.util.concurrent.ConcurrentHashMap

class MapConditionsState : ConditionsState {

    val map = ConcurrentHashMap<String, Boolean>()

    override fun doMet(condition: Condition<*>) {
        map[condition.id] = true
    }

    override fun doNotMet(condition: Condition<*>) {
        map[condition.id] = false
    }

    override fun allMet(): Boolean {
        return map.values.all { it }
    }
}