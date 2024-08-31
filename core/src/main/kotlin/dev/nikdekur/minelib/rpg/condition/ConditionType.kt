package dev.nikdekur.minelib.rpg.condition

import kotlinx.serialization.Serializable

@Serializable
abstract class ConditionType<C> {
    abstract val contextClass: Class<C>
}