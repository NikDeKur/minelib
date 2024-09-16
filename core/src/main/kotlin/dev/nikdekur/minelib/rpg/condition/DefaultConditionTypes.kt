package dev.nikdekur.minelib.rpg.condition

import dev.nikdekur.minelib.movement.OptiPlayerMoveEvent
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerChangedWorldEvent

object DefaultConditionTypes {

    inline fun <reified C> new() = object : ConditionType<C> {
        override val contextClass = C::class.java

        override fun toString(): String {
            return "ConditionType(${contextClass.simpleName})"
        }
    }

    val INVENTORY = new<Event>()
    val WORLD = new<PlayerChangedWorldEvent>()
    val LOCATION = new<OptiPlayerMoveEvent>()
}