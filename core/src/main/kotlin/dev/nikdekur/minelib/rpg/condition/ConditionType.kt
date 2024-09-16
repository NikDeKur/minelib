package dev.nikdekur.minelib.rpg.condition

interface ConditionType<C> {
    val contextClass: Class<C>
}