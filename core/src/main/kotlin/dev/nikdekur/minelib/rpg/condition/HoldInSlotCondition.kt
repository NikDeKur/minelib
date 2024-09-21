@file:OptIn(ExperimentalSerializationApi::class)

package dev.nikdekur.minelib.rpg.condition

import dev.nikdekur.minelib.inventory.InventorySlot
import dev.nikdekur.minelib.utils.debug
import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerItemHeldEvent

@Serializable
@SerialName("HoldInSlot")
data class HoldInSlotCondition(val slot: InventorySlot) : Condition<@Contextual Event>() {

    override val id = "HoldInHand"

    override val cType = DefaultConditionTypes.INVENTORY

    override fun isSatisfied(context: Event): Boolean {
        debug("checking HoldInSlotCondition")
        val context = context as? PlayerItemHeldEvent ?: return false
        debug("actualSlot: ${context.newSlot}, requiredSlot: ${slot.index}")
        val newSlot = context.newSlot
        val isSatisfied =
            if (slot == InventorySlot.MAIN_HAND)
                context.player.inventory.heldItemSlot == newSlot
            else slot.index == newSlot

        debug("isSatisfied: $isSatisfied")

        return isSatisfied
    }
}