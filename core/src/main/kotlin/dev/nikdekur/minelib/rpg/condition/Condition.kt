package dev.nikdekur.minelib.rpg.condition

import dev.nikdekur.ndkore.`interface`.Snowflake
import kotlinx.serialization.Serializable

/**
 * Represents a condition that can be satisfied or not.
 *
 * @param C The context type.
 */
@Serializable
abstract class Condition<C> : Snowflake<String> {

    abstract val cType: ConditionType<C>

    abstract fun isSatisfied(context: C): Boolean
}