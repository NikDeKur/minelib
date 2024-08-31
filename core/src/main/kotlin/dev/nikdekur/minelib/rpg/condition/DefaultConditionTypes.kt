package dev.nikdekur.minelib.rpg.condition

import dev.nikdekur.minelib.movement.OptiPlayerMoveEvent
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerChangedWorldEvent

object DefaultConditionTypes {

    val INVENTORY = object : ConditionType<Event>() {
        override val contextClass = Event::class.java
    }

    val WORLD = object : ConditionType<PlayerChangedWorldEvent>() {
        override val contextClass = PlayerChangedWorldEvent::class.java
    }

    val LOCATION = object : ConditionType<OptiPlayerMoveEvent>() {
        override val contextClass = OptiPlayerMoveEvent::class.java
    }
}