package dev.nikdekur.minelib.rpg.condition

interface ConditionsState {

    fun doMet(condition: Condition<*>)
    fun doNotMet(condition: Condition<*>)

    fun allMet(): Boolean
}